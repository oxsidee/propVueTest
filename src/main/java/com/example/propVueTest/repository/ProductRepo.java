package com.example.propVueTest.repository;

import com.example.propVueTest.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepo extends JpaRepository<Product, String> {
}
