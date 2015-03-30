package org.christor.ordersystem.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.christor.ordersystem.model.CustomerOrder;
import org.christor.ordersystem.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("restock/orders")
public class RestockOrdersRestResource {

    @Autowired
    private BaseUrlExtractionFilter baseUrlFilter;

    @Autowired
    private RestockOrdersRestRepository ordersRepository;

    @Autowired
    private ProductsRestRepository productsRepository;

    @RequestMapping(method = RequestMethod.GET)
    public Page<CustomerOrder> getOrderList(Pageable pageable) {
        return ordersRepository.findAll(pageable);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity getRestockOrderById(@PathVariable("id") Long id) {
        CustomerOrder entity = ordersRepository.findOne(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(entity);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createRestockOrder(@RequestBody CustomerOrder order) throws URISyntaxException {

        List<Product> products = order.getProducts();

        // build a map of product ids and counts ordered
        Map<Long, Long> productCountMap = products.stream().collect(Collectors.groupingBy(RestockOrdersRestResource::getProductId, Collectors.counting()));

        // Now update all related entities in preparation for creating the restock order
        order.getProducts().clear();
        productCountMap.entrySet().stream().forEach((productOrderQty) -> {
            Long productId = productOrderQty.getKey();
            Long productQuantity = productOrderQty.getValue();
            Product product = productsRepository.findById(productId).orElse(Product.NO_SUCH_PRODUCT);
            product.setStock(product.getStock() + productQuantity);
            for (int i = 0; i < productQuantity; i++) {
                order.getProducts().add(product);
            }
            productsRepository.save(product);
        });

        // Return our response
        final Long newId = ordersRepository.save(order).getId();
        return ResponseEntity.created(new URI(baseUrlFilter.getBaseUrl() + "restock/orders/" + newId)).build();
    }

    private static Long getProductId(Product p) {
        String href = p.getHref();
        int lastSlash = href.lastIndexOf("/");
        String idString = href.substring(lastSlash + 1);
        return Long.parseLong(idString);
    }

}
