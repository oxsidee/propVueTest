package com.example.propVueTest.controller;

import com.example.propVueTest.domain.Product;
import com.example.propVueTest.domain.ProductStatus;
import com.example.propVueTest.repository.ProductRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductRepo productRepo;

    @InjectMocks
    ProductController productController;

    @Test
    @DisplayName("Get all products")
    void getAllProducts_noFilter() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Product> products = List.of(
                new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0),
                new Product("p2", ProductStatus.Inbound, "fc2", 422, 1338.0),
                new Product("p3", ProductStatus.Unfulfillable, "fc3", 422, 1339.0)
                );
        doReturn(products).when(productRepo).findAll();

        List<Product> list = this.productController.getAllProducts(new HashMap<>());

        assertNotNull(list);
        assertEquals(products, list);
    }

    @Test
    @DisplayName("Get all products with 1 filter")
    void getAllProducts_oneFilter() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Product> products = List.of(
                new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0)
        );

        Product productExample = new Product("p1", null, null, null, null);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreNullValues();
        Example<Product> example = Example.of(productExample, matcher);
        doReturn(products).when(productRepo).findAll(example);

        Map<String, String> map = new HashMap<>();
        map.put("productId", "p1");
        List<Product> list = this.productController.getAllProducts(map);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("p1", list.get(0).getProductId());
    }

    @Test
    @DisplayName("Get all products with 2 filters")
    void getAllProducts_twoFilters() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Product> products = List.of(
                new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0),
                new Product("p4", ProductStatus.Sellable, "fc1", 422, 1339.0)
        );
        Product productExample = new Product(null, ProductStatus.Sellable, "fc1", null, null);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreNullValues();
        Example<Product> example = Example.of(productExample, matcher);
        doReturn(products).when(productRepo).findAll(example);

        Map<String, String> map = new HashMap<>();
        map.put("status", "Sellable");
        map.put("fulfillmentCenter", "fc1");
        List<Product> list = this.productController.getAllProducts(map);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Get all products values")
    void getProductValues() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Product> products = List.of(
                new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0),
                new Product("p2", ProductStatus.Inbound, "fc2", 422, 1338.0),
                new Product("p3", ProductStatus.Unfulfillable, "fc3", 422, 1339.0)
        );
        doReturn(products).when(productRepo).findAll();
        Map<String, Double> map = this.productController.getProductValues(new HashMap<>());
        assertEquals(4014.0, map.get("sum"));
    }

    @Test
    @DisplayName("Create new product")
    void createProduct() {
        String fulfillmentCenter = "fc4";
        Integer quantity =  420;
        Double value = 1338.0;
        Product newProduct = new Product(null, ProductStatus.Sellable, fulfillmentCenter, quantity,value);
        Product createdProduct = new Product("p1", ProductStatus.Sellable, fulfillmentCenter, quantity,value);

        doReturn(createdProduct).when(productRepo).save(newProduct);

        Product savedProduct = this.productController.createProduct(newProduct);

        assertNotNull(savedProduct);
        assertEquals("p1", savedProduct.getProductId());
        assertEquals(ProductStatus.Sellable, savedProduct.getStatus());
        assertEquals(fulfillmentCenter, savedProduct.getFulfillmentCenter());
        assertEquals(quantity, savedProduct.getQuantity());
        assertEquals(value, savedProduct.getValue());

    }

    @Test
    @DisplayName("Get one product")
    void testGetProduct() {
        String fulfillmentCenter = "fc4";
        Integer quantity =  420;
        Double value = 1338.0;
        Product newProduct = new Product("p1", ProductStatus.Sellable, fulfillmentCenter, quantity,value);

        Product gettedProduct = this.productController.getProduct(newProduct);
        assertNotNull(gettedProduct);
        assertEquals("p1", gettedProduct.getProductId());
        assertEquals(ProductStatus.Sellable, gettedProduct.getStatus());
        assertEquals(fulfillmentCenter, gettedProduct.getFulfillmentCenter());
        assertEquals(quantity, gettedProduct.getQuantity());
        assertEquals(value, gettedProduct.getValue());
    }

    @Test
    @DisplayName("Update product")
    void updateProduct() {
        Product oldProduct = new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0);
        Product newProduct = new Product("p1", ProductStatus.Inbound, "fc2", 422, 1339.0);
        doReturn(newProduct).when(productRepo).save(newProduct);

        Product updatedProduct = this.productController.updateProduct(newProduct, oldProduct);

        assertNotNull(updatedProduct);
        assertEquals("p1", updatedProduct.getProductId());
        assertEquals(ProductStatus.Inbound, updatedProduct.getStatus());
        assertEquals(newProduct.getFulfillmentCenter(), updatedProduct.getFulfillmentCenter());
        assertEquals(newProduct.getQuantity(), updatedProduct.getQuantity());
        assertEquals(newProduct.getValue(), updatedProduct.getValue());
    }

    @Test
    @DisplayName("Delete product")
    void deleteProduct() {
        Product product = new Product("p1", ProductStatus.Sellable, "fc1", 421, 1337.0);
        Map<String, Boolean> map = new HashMap<>();
        map.put("deleted", true);

        Map<String, Boolean> result = this.productController.deleteProduct(product);

        assertNotNull(result);
        assertEquals(map, result);
    }
}