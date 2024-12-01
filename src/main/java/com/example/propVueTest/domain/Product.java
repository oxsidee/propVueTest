package com.example.propVueTest.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-id", strategy = "com.example.propVueTest.util.CustomIdGenerator")
    @Column(name = "product_id")
    private String productId;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private String fulfillmentCenter;
    private Integer quantity;
    private Double value;
}
