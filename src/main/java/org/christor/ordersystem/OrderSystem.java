package org.christor.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
