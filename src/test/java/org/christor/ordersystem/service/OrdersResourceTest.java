package org.christor.ordersystem.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.christor.ordersystem.OrderSystem;
import org.christor.ordersystem.model.CustomerOrder;
import org.christor.ordersystem.model.Product;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.After;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OrderSystem.class)
@WebAppConfiguration
public class OrdersResourceTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private final List<CustomerOrder> orderList = new ArrayList<>();
    private final List<Product> productsList = new ArrayList<>();

    @Autowired
    private ProductsRestRepository productsRepository;

    @Autowired
    private OrdersRestRepository ordersRepository;

    @Autowired
    private BaseUrlExtractionFilter baseUrlProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.baseUrlProvider.baseUrl = "http://localhost/";
        this.ordersRepository.deleteAll();
        this.productsRepository.deleteAll();
        this.productsList.add(productsRepository.save(
                Product.builder()
                .name("Widget 1")
                .description("The first widget in our test set")
                .price(100)
                .stock(10L)
                .build()));
        this.productsList.add(productsRepository.save(
                Product.builder()
                .name("Widget 2")
                .description("The second widget in our test set")
                .price(1000)
                .stock(100L)
                .build()));
        this.productsList.forEach(p -> {
            p.updateUri(baseUrlProvider.getBaseUrl());
        });
        this.orderList.add(ordersRepository.save(CustomerOrder.builder()
                .name("ralph")
                .address("328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(convertProductListToStringList())
                .build()));
        this.orderList.add(ordersRepository.save(CustomerOrder.builder()
                .name("alice")
                .address("328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(convertProductListToStringList())
                .build()));

    }

    private List<Product> convertProductListToStringList() {
//        return this.productsList.stream().map(p -> CustomerOrderLine.builder().product(p).build()).collect(Collectors.toList());
        return this.productsList;
    }

    @After
    public void teardown() throws Exception {
        this.ordersRepository.deleteAll();
        this.productsRepository.deleteAll();
        this.orderList.clear();
    }

    @Test
    public void readOrderOneAtATime() throws Exception {
        for (int i = 0; i < orderList.size(); i++) {
            final CustomerOrder order = this.orderList.get(i);
            mockMvc.perform(get("/orders/" + (i + firstOrderId())).accept(contentType))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$.id", is(order.getId().intValue())))
                    .andExpect(jsonPath("$.name", is(order.getName())))
                    .andExpect(jsonPath("$.address", is(order.getAddress())))
                    .andExpect(jsonPath("$.products", hasSize(getProductList(order).size())))
                    .andExpect(jsonPath("$.orderDate", is(order.getOrderDate().getTime())));
        }
    }

    @Test
    public void orderNotFound() throws Exception {
        mockMvc.perform(get("/orders/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readOrdersList() throws Exception {
        ResultActions result = mockMvc.perform(get("/orders").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(2)));
        for (int i = 0; i < orderList.size(); i++) {
            final CustomerOrder order = this.orderList.get(i);
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$.content[" + i + "].id", is(order.getId().intValue())))
                    .andExpect(jsonPath("$.content[" + i + "].name", is(order.getName())))
                    .andExpect(jsonPath("$.content[" + i + "].address", is(order.getAddress())))
                    .andExpect(jsonPath("$.content[" + i + "].products", hasSize(getProductList(order).size())))
                    .andExpect(jsonPath("$.content[" + i + "].orderDate", is(order.getOrderDate().getTime())));
        }

    }

    @Test
    public void createSimpleOrder() throws Exception {

        int expectedId = firstOrderId().intValue() + orderList.size();
        final CustomerOrder orderToAdd = CustomerOrder.builder()
                .name("ed")
                .address("328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(convertProductListToStringList())
                .build();
        String orderJson = json(orderToAdd);

        // first make sure it's not there...
        mockMvc.perform(get("/orders/" + expectedId))
                .andExpect(status().isNotFound());

        // then add it...
        this.mockMvc.perform(post("/orders")
                .contentType(contentType)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/orders/" + expectedId));

        // then make sure it's there...
        mockMvc.perform(get("/orders/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(orderToAdd.getName())))
                .andExpect(jsonPath("$.address", is(orderToAdd.getAddress())))
                .andExpect(jsonPath("$.products", hasSize(getProductList(orderToAdd).size())))
                .andExpect(jsonPath("$.orderDate", is(orderToAdd.getOrderDate().getTime())));

        // Verify stock levels are reduced
        Assert.assertEquals("Item 1's stock should be reduced", 9L, productsRepository.findOne(productsList.get(0).getId()).getStock().longValue());
        Assert.assertEquals("Item 2's stock should be reduced", 99L, productsRepository.findOne(productsList.get(1).getId()).getStock().longValue());
    }

    @Test
    public void createComplexOrder() throws Exception {

        ArrayList<Product> products = new ArrayList<>();
        products.addAll(convertProductListToStringList());
        products.addAll(convertProductListToStringList());
        products.addAll(convertProductListToStringList());
        int expectedId = firstOrderId().intValue() + orderList.size();
        final CustomerOrder orderToAdd = CustomerOrder.builder()
                .name("ed")
                .address("328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(products)
                .build();
        String orderJson = json(orderToAdd);

        // first make sure it's not there...
        mockMvc.perform(get("/orders/" + expectedId))
                .andExpect(status().isNotFound());

        // then add it...
        this.mockMvc.perform(post("/orders")
                .contentType(contentType)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/orders/" + expectedId));

        // then make sure it's there...
        mockMvc.perform(get("/orders/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(orderToAdd.getName())))
                .andExpect(jsonPath("$.address", is(orderToAdd.getAddress())))
                .andExpect(jsonPath("$.products", hasSize(getProductList(orderToAdd).size())))
                .andExpect(jsonPath("$.orderDate", is(orderToAdd.getOrderDate().getTime())));

        // Verify stock levels are reduced
        Assert.assertEquals("Item 1's stock should be reduced by three", 7L, productsRepository.findOne(productsList.get(0).getId()).getStock().longValue());
        Assert.assertEquals("Item 2's stock should be reduced bt three", 97L, productsRepository.findOne(productsList.get(1).getId()).getStock().longValue());
    }

    @Test
    public void orderThatExhaustsStockIsAllowed() throws Exception {

        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.addAll(convertProductListToStringList());
        }
        int expectedId = firstOrderId().intValue() + orderList.size();
        final CustomerOrder orderToAdd = CustomerOrder.builder()
                .name("ed")
                .address("328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(products)
                .build();
        String orderJson = json(orderToAdd);

        // first make sure it's not there...
        mockMvc.perform(get("/orders/" + expectedId))
                .andExpect(status().isNotFound());

        // then add it...
        this.mockMvc.perform(post("/orders")
                .contentType(contentType)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/orders/" + expectedId));

        // then make sure it's there...
        mockMvc.perform(get("/orders/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(orderToAdd.getName())))
                .andExpect(jsonPath("$.address", is(orderToAdd.getAddress())))
                .andExpect(jsonPath("$.products", hasSize(getProductList(orderToAdd).size())))
                .andExpect(jsonPath("$.orderDate", is(orderToAdd.getOrderDate().getTime())));

        // Verify stock levels are reduced
        Assert.assertEquals("Item 1's stock should be reduced by ten", 0L, productsRepository.findOne(productsList.get(0).getId()).getStock().longValue());
        Assert.assertEquals("Item 2's stock should be reduced by ten", 90L, productsRepository.findOne(productsList.get(1).getId()).getStock().longValue());
    }

    @Test
    public void orderThatExcedsStockIsNotAllowed() throws Exception {

        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            products.addAll(convertProductListToStringList());
        }
        int expectedId = firstOrderId().intValue() + orderList.size();
        final CustomerOrder orderToAdd = CustomerOrder.builder()
                .name("ed")
                .address("328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214")
                .orderDate(new Date())
                .products(products)
                .build();
        String orderJson = json(orderToAdd);

        // first make sure it's not there...
        mockMvc.perform(get("/orders/" + expectedId))
                .andExpect(status().isNotFound());

        // then add it...
        this.mockMvc.perform(post("/orders")
                .contentType(contentType)
                .content(orderJson))
                .andExpect(status().isBadRequest());

        // then make sure it's NOT there...
        mockMvc.perform(get("/orders/" + expectedId).accept(contentType))
                .andExpect(status().isNotFound());

        // Verify stock levels are unaffected
        Assert.assertEquals("Item 1's stock should be unaffected", 10L, productsRepository.findOne(productsList.get(0).getId()).getStock().longValue());
        Assert.assertEquals("Item 2's stock should be unaffected", 100L, productsRepository.findOne(productsList.get(1).getId()).getStock().longValue());
    }

    @Test
    public void createMalformedProductBody() throws Exception {

        final CustomerOrder productToAdd = CustomerOrder.builder()
                .name("womp womp womp")
                .build();
        String productJson = json(productToAdd + "}");

        // then add it...
        this.mockMvc.perform(post("/orders")
                .contentType(contentType)
                .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithIncorrectContentType() throws Exception {

        final CustomerOrder productToAdd = CustomerOrder.builder()
                .name("womp womp womp")
                .build();
        String productJson = json(productToAdd);

        // then TRY to add it...
        this.mockMvc.perform(post("/orders")
                .contentType("blah/blah")
                .content(productJson))
                .andExpect(status().isUnsupportedMediaType());
    }

    private static List<Product> getProductList(final CustomerOrder orderToAdd) {
        List<Product> products = orderToAdd.getProducts();
        if (products == null) {
            products = Collections.EMPTY_LIST;
        }
        return products;
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private Long firstOrderId() {
        return this.orderList.get(0).getId();
    }

}
