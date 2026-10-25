package com.example.onlineshop.repository;

import com.example.onlineshop.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String keyword);

    List<Product> findByCategory_Subcategory(String subcategory);

    List<Product> findByCategory_SubcategoryAndNameContaining(String subcategory, String name);

    @EntityGraph(attributePaths = {"skus"})
    Optional<Product> findById(Long productId);

    @EntityGraph(attributePaths = {"skus"})
    List<Product> findAll();

    List<Product> findByCategory_SubcategoryIn(List<String> subcategoryNames);

}
