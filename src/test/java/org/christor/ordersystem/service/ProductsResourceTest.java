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
import java.util.List;
import org.christor.ordersystem.OrderSystem;
import org.christor.ordersystem.model.Product;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.junit.After;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OrderSystem.class)
@WebAppConfiguration
public class ProductsResourceTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private final List<Product> productList = new ArrayList<>();

    @Autowired
    private ProductsRestRepository productRepository;

    @Autowired
    private OrdersRestRepository ordersRepository;

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

        this.ordersRepository.deleteAll();
        this.productRepository.deleteAll();
        this.productList.add(productRepository.save(
                Product.builder()
                .name("Widget 1")
                .description("The first widget in our test set")
                .price(100)
                .stock(10L)
                .build()));
        this.productList.add(productRepository.save(
                Product.builder()
                .name("Widget 2")
                .description("The second widget in our test set")
                .price(1000)
                .stock(100L)
                .build()));
    }

    @After
    public void teardown() throws Exception {
        this.ordersRepository.deleteAll();
        this.productRepository.deleteAll();
        this.productList.clear();
    }

    @Test
    public void readProductOneAtATime() throws Exception {
        for (int i = 0; i < productList.size(); i++) {
            final Product product = this.productList.get(i);
            mockMvc.perform(get("/products/" + (i + firstProductId())).accept(contentType))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                    .andExpect(jsonPath("$.name", is(product.getName())))
                    .andExpect(jsonPath("$.description", is(product.getDescription())))
                    .andExpect(jsonPath("$.price", is(product.getPrice())))
                    .andExpect(jsonPath("$.stock", is(product.getStock().intValue())));
        }
    }

    @Test
    public void productNotFound() throws Exception {
        mockMvc.perform(get("/products/-1").accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readProductsList() throws Exception {
        ResultActions result = mockMvc.perform(get("/products").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products", hasSize(2)));
        for (int i = 0; i < productList.size(); i++) {
            final Product product = this.productList.get(i);
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$['_embedded'].products[" + i + "].['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                    .andExpect(jsonPath("$['_embedded'].products[" + i + "].name", is(product.getName())))
                    .andExpect(jsonPath("$['_embedded'].products[" + i + "].description", is(product.getDescription())))
                    .andExpect(jsonPath("$['_embedded'].products[" + i + "].price", is(product.getPrice())))
                    .andExpect(jsonPath("$['_embedded'].products[" + i + "].stock", is(product.getStock().intValue())));
        }

    }

    @Test
    public void testSearchByNameContaining2() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/search/findByNameContaining?name=2").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products", hasSize(1)));
        final Product product = this.productList.get(1);
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products[0].['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                .andExpect(jsonPath("$['_embedded'].products[0].name", is(product.getName())))
                .andExpect(jsonPath("$['_embedded'].products[0].description", is(product.getDescription())))
                .andExpect(jsonPath("$['_embedded'].products[0].price", is(product.getPrice())))
                .andExpect(jsonPath("$['_embedded'].products[0].stock", is(product.getStock().intValue())));
    }

    
    @Test
    public void testSearchByNameContaining1() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/search/findByNameContaining?name=1").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products", hasSize(1)));
        final Product product = this.productList.get(0);
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products[0].['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                .andExpect(jsonPath("$['_embedded'].products[0].name", is(product.getName())))
                .andExpect(jsonPath("$['_embedded'].products[0].description", is(product.getDescription())))
                .andExpect(jsonPath("$['_embedded'].products[0].price", is(product.getPrice())))
                .andExpect(jsonPath("$['_embedded'].products[0].stock", is(product.getStock().intValue())));
    }
    
    @Test
    public void testSearchByNameContainingEAndPriceLessThan500() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/search/findByNameContainingAndPriceLessThan?name=e&price=500").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products", hasSize(1)));
        final Product product = this.productList.get(0);
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products[0].['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                .andExpect(jsonPath("$['_embedded'].products[0].name", is(product.getName())))
                .andExpect(jsonPath("$['_embedded'].products[0].description", is(product.getDescription())))
                .andExpect(jsonPath("$['_embedded'].products[0].price", is(product.getPrice())))
                .andExpect(jsonPath("$['_embedded'].products[0].stock", is(product.getStock().intValue())));
    }
    
    @Test
    public void testSearchByDescriptionContainingFirst() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/search/findByDescriptionContaining?desc=first").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products", hasSize(1)));
        final Product product = this.productList.get(0);
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_embedded'].products[0].['_links'].self.href", is("http://localhost/products/" + product.getId().intValue())))
                .andExpect(jsonPath("$['_embedded'].products[0].name", is(product.getName())))
                .andExpect(jsonPath("$['_embedded'].products[0].description", is(product.getDescription())))
                .andExpect(jsonPath("$['_embedded'].products[0].price", is(product.getPrice())))
                .andExpect(jsonPath("$['_embedded'].products[0].stock", is(product.getStock().intValue())));
    }
    
    @Test
    public void createProduct() throws Exception {

        int expectedId = firstProductId() + productList.size();
        final Product product = Product.builder()
                .name("Widget 3 -- created")
                .description("The third and newest widget in our test set (Welcome aboard!)")
                .price(150)
                .stock(2L)
                .build();
        String productJson = json(product);

        // first make sure it's not there...
        mockMvc.perform(get("/products/" + expectedId).accept(contentType))
                .andExpect(status().isNotFound());

        // then add it...
        this.mockMvc.perform(post("/products").accept(contentType)
                .contentType(contentType)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/products/" + expectedId));

        // then make sure it's there...
        mockMvc.perform(get("/products/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_links'].self.href", is("http://localhost/products/" + expectedId)))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice())))
                .andExpect(jsonPath("$.stock", is(product.getStock().intValue())));
    }

    @Test
    public void updateProduct() throws Exception {

        int expectedId = firstProductId();
        final Product product = Product.builder()
                .name("Widget 3 -- updated")
                .description("The third and newest widget in our test set (Welcome aboard!)")
                .price(150)
                .stock(2L)
                .build();
        String productJson = json(product);

        // first make sure it IS there AND its description isn't what we're setting it to
        mockMvc.perform(get("/products/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_links'].self.href", is("http://localhost/products/" + expectedId)))
                .andExpect(jsonPath("$.name", not(is("Widget 3 -- updated"))));

        // then add it...
        this.mockMvc.perform(put("/products/" + expectedId).accept(contentType)
                .contentType(contentType)
                .content(productJson))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Location", "http://localhost/products/" + expectedId));

        // then make sure it's there...
        mockMvc.perform(get("/products/" + expectedId).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$['_links'].self.href", is("http://localhost/products/" + expectedId)))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice())))
                .andExpect(jsonPath("$.stock", is(product.getStock().intValue())));
    }

    @Test
    public void createMalformedProductBody() throws Exception {

        final Product productToAdd = Product.builder()
                .name("womp womp womp")
                .description("This should not be created")
                .price(150)
                .stock(2L)
                .build();
        String productJson = json(productToAdd + "}");

        // then add it...
        this.mockMvc.perform(post("/products")
                .accept(contentType)
                .contentType(contentType)
                .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithIncorrectContentType() throws Exception {

        final Product productToAdd = Product.builder()
                .name("womp womp womp")
                .description("This should not be created")
                .price(150)
                .stock(2L)
                .build();
        String productJson = json(productToAdd);

        // then add it...
        this.mockMvc.perform(post("/products")
                .accept(contentType)
                .contentType(MediaType.APPLICATION_XML)
                .content(productJson))
                .andExpect(status().isBadRequest());
        // XXX: This should really be isUnsupportedMediaType, but Spring returns 400 instead of 415
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private int firstProductId() {
        return this.productList.get(0).getId().intValue();
    }
}
