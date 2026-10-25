package com.example.onlineshop.repository;

import com.example.onlineshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Category.CategoryPK> {
    Optional<Category> findByCategoryAndSubcategory(String category, String subcategory);

    List<Category> findByCategory(String category);
}
