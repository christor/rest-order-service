package org.christor.ordersystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.christor.ordersystem.model.CustomerOrder;
import org.christor.ordersystem.model.Product;
import org.christor.ordersystem.model.RestockOrder;
import org.christor.ordersystem.service.OrdersRestRepository;
import org.christor.ordersystem.service.ProductsRestRepository;
import org.christor.ordersystem.service.RestockOrdersRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * Main driver of the application, loads spring and causes server to start.
 *
 * @author crued
 */
@SpringBootApplication
public class OrderSystem {

    public static void main(String[] args) {
        SpringApplication.run(OrderSystem.class, args);
    }

    private final List<Product> productList = new ArrayList<>();

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private OrdersRestRepository ordersRespository;

    @Autowired
    private RestockOrdersRestRepository restockOrdersRespository;

    @Autowired
    private ProductsRestRepository productsRespository;

    boolean createSampleData = false;

    @PostConstruct
    @Transactional
    public void postConstruct() {
        if (createSampleData) {
            this.productList.add(productsRespository.save(
                    Product.builder()
                    .name("Widget 1")
                    .description("The first widget in our test set")
                    .price(100)
                    .stock(10L)
                    .build()));
            this.productList.add(productsRespository.save(
                    Product.builder()
                    .name("Widget 2")
                    .description("The second widget in our test set")
                    .price(1000)
                    .stock(100L)
                    .build()));

            ordersRespository.save(CustomerOrder.builder()
                    .name("ralph")
                    .address("328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214")
                    .orderDate(new Date())
                    .products(Arrays.asList(productList.get(0)))
                    .build());
            ordersRespository.save(CustomerOrder.builder()
                    .name("alice")
                    .address("328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214")
                    .orderDate(new Date())
                    .products(Arrays.asList(productList.get(1)))
                    .build());
            ordersRespository.save(CustomerOrder.builder()
                    .name("ed")
                    .address("328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214")
                    .orderDate(new Date())
                    .products(productList)
                    .build());
            ordersRespository.save(CustomerOrder.builder()
                    .name("ralph")
                    .address("328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214")
                    .orderDate(new Date())
                    .products(Arrays.asList(productList.get(0), productList.get(1), productList.get(1), productList.get(1), productList.get(1)))
                    .build());
            restockOrdersRespository.save(RestockOrder.builder()
                    .products(productList)
                    .build());
            restockOrdersRespository.save(RestockOrder.builder()
                    .products(productList)
                    .build());
            restockOrdersRespository.save(RestockOrder.builder()
                    .products(productList)
                    .build());
            restockOrdersRespository.save(RestockOrder.builder()
                    .products(productList)
                    .build());
        }
    }

}
