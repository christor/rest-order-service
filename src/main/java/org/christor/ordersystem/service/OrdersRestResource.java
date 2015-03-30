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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides a RESTful service for operations on Order entities.
 * 
 * @author crued
 */
@RestController
@RequestMapping("orders")
public class OrdersRestResource {

    @Autowired
    private BaseUrlExtractionFilter baseUrlFilter;

    @Autowired
    private OrdersRestRepository ordersRepository;

    @Autowired
    private ProductsRestRepository productsRepository;

    /**
     * Returns a paged list of results for all orders, or (if a user is specified)
     * all orders for a given user.
     * 
     * @param user optional parameter indicating the user to match.
     * @param pageable the current paging state to drive which results are returned.
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public Page<CustomerOrder> getOrderList(@RequestParam(required = false, value = "user") String user, Pageable pageable) {
        if (user == null) {
            return ordersRepository.findAll(pageable);
        } else {
            return ordersRepository.findByName(user, pageable);
        }
    }

    /**
     * Returns a single Order, whose id is specified, or responds with a 404.
     * 
     * @param id the order id.
     * @return a representation of the order.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity getOrderById(@PathVariable("id") Long id) {
        CustomerOrder entity = ordersRepository.findOne(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(entity);
        }
    }

    /**
     * Attempts to create an order. This will either succeed and return a 201 (created)
     * along with a location header indicating the URL where this entity can be
     * retrieved, or responds with BAD REQUEST (400) if there is insufficient stock
     * on any of the items in the order. As a side-effect, the stock level for all 
     * items in a successfully-created order are adjusted accordingly.
     * 
     * @param order A representation of the order to create.
     * @return a response indicating if the creation of the order succeeded.
     * @throws URISyntaxException 
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createOrder(@RequestBody CustomerOrder order) throws URISyntaxException {

        List<Product> products = order.getProducts();

        // build a map of product ids and counts ordered
        Map<Long, Long> productCountMap = products.stream().collect(Collectors.groupingBy(OrdersRestResource::getProductId, Collectors.counting()));

        // check whether we have sufficient stock, responding with BAD REQUEST if not
        for (Map.Entry<Long, Long> productOrderQty : productCountMap.entrySet()) {
            Long productId = productOrderQty.getKey();
            Long productQuantity = productOrderQty.getValue();
            Product product = productsRepository.findById(productId).orElse(Product.NO_SUCH_PRODUCT);
            Long stock = product.getStock();
            if (stock < productQuantity) {
                return ResponseEntity.badRequest().body("Insufficient stock for " + productId);
            }
        }

        // Now update all related entities in preparation for creating the order
        order.getProducts().clear();
        productCountMap.entrySet().stream().forEach((productOrderQty) -> {
            Long productId = productOrderQty.getKey();
            Long productQuantity = productOrderQty.getValue();
            Product product = productsRepository.findById(productId).orElse(Product.NO_SUCH_PRODUCT);
            product.setStock(product.getStock() - productQuantity);
            for (int i = 0; i < productQuantity; i++) {
                order.getProducts().add(product);
            }
            productsRepository.save(product);
        });

        // Return our response
        final Long newId = ordersRepository.save(order).getId();
        return ResponseEntity.created(new URI(baseUrlFilter.getBaseUrl() + "orders/" + newId)).build();
    }

    /**
     * A helper function to find the product id based on its URL.
     * There's probably a nicer way than this...
     * 
     * @param p the product
     * @return the id
     */
    private static Long getProductId(Product p) {
        String href = p.getHref();
        int lastSlash = href.lastIndexOf("/");
        String idString = href.substring(lastSlash + 1);
        return Long.parseLong(idString);
    }

}
