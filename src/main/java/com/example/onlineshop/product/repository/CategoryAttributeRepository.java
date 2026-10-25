package com.example.onlineshop.product.repository;

import com.example.onlineshop.product.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {
    List<CategoryAttribute> findAllByOrderByCategoryAscSubcategoryAscNameAsc();

    boolean existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCase(
            String category, String subcategory, String name);

        boolean existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCaseAndIdNot(
            String category, String subcategory, String name, Long id);
}
