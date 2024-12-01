package com.example.propVueTest.controller;

import com.example.propVueTest.domain.Product;
import com.example.propVueTest.repository.ProductRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@Tag(name = "ProductController")
public class ProductController {
    final private ProductRepo productRepo;

    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Operation(
            description = "Получает список всех продуктов",
            summary = "Запрос может содержать параметры." +
                    "Параметры являются фильтрами. " +
                    "Имя параметра должно соотносится с любым из полей самого продукта. " +
                    "Значение параметра должно содержать в себе текст по которому продукты нужно отфильтровать" +
                    "Пример: /product?productId=p4&value=1800&status=Sellable" +
                    "В случае, если фильтры не заполнены, сервер вернет список всех продуктов"
    )
    @GetMapping
    public List<Product> getAllProducts(@RequestParam(required = false) Map<String, String> params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (params != null && !params.isEmpty()) {
            Example<Product> example = getProductExample(params);
            return productRepo.findAll(example);
        }
        return productRepo.findAll();
    }

    @Operation(
            description = "Получает сумму всех продуктов",
            summary = "Запрос может содержать параметры." +
                    "Параметры являются фильтрами. " +
                    "Имя параметра должно соотносится с любым из полей самого продукта. " +
                    "Значение параметра должно содержать в себе текст по которому продукты нужно отфильтровать" +
                    "Пример: /product/valueSum?&status=Sellable&quantity=10" +
                    "В случае, если фильтры не заполнены, то сервер вернет сумму всех продуктов"
    )
    @GetMapping("valueSum")
    public Map<String,Double> getProductValues(@RequestParam(required = false) Map<String, String> params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Product> products;
        if (params != null && !params.isEmpty()) {
            Example<Product> example = getProductExample(params);
            products = productRepo.findAll(example);
        } else {
            products = productRepo.findAll();
        }
        Double sum = 0.0;
        for (Product product : products) {
            sum += product.getValue();
        }
        Map<String,Double> map = new HashMap<>();
        map.put("sum", sum);
        return map;
    }

    @Operation(
            description = "Создает новый продукт",
            summary = "ID продукта генерируется по схеме 'р'+уникальный номер объекта"
    )
    @PostMapping()
    public Product createProduct(@RequestBody Product product) {
        product = productRepo.save(product);
        return product;
    }

    @Operation(
            description = "Находит продукт по его ID"
    )
    @GetMapping("{product}")
    public Product getProduct(@PathVariable Product product) {
        return product;
    }

    @Operation(
            description = "Обновляет продукт по его ID"
    )
    @PostMapping("{productFromDB}")
    public Product updateProduct(@RequestBody Product product, @PathVariable Product productFromDB) {
        BeanUtils.copyProperties(product, productFromDB, "productId");
        productFromDB = productRepo.save(productFromDB);
        return productFromDB;
    }

    @Operation(
            description = "Удаляет продукт по его ID"
    )

    @DeleteMapping("{productFromDB}")
    public Map<String, Boolean> deleteProduct(@PathVariable Product productFromDB) {
        productRepo.delete(productFromDB);
        Map<String, Boolean> map = new HashMap<>();
        map.put("deleted", Boolean.TRUE);
        return map;
    }

    @Hidden
    private static Example<Product> getProductExample(Map<String, String> params) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> objectMap = objectMapper.convertValue(Product.class.getDeclaredConstructor().newInstance(), new TypeReference<Map<String, Object>>() {});
        objectMap.putAll(params);
        Product productExample = objectMapper.convertValue(objectMap, Product.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreNullValues();
        return Example.of(productExample, matcher);
    }
}
