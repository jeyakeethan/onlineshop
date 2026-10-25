package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private String category;
    private String subcategory;
    private String slug;
    private List<String> breadcrumb = new ArrayList<>();

    public CategoryDTO(Category category) {
        this.category = category.getCategory();
        this.subcategory = category.getSubcategory();
        this.slug = buildSlug(this.category, this.subcategory);
        this.breadcrumb = buildBreadcrumb(this.category, this.subcategory);
    }

    public static String buildSlug(String category, String subcategory) {
        String base = (subcategory == null || subcategory.isBlank())
                ? category
                : category + "-" + subcategory;
        return slugify(base);
    }

    public static List<String> buildBreadcrumb(String category, String subcategory) {
        List<String> breadcrumb = new ArrayList<>();
        if (category != null && !category.isBlank()) {
            breadcrumb.add(category);
        }
        if (subcategory != null && !subcategory.isBlank()) {
            breadcrumb.add(subcategory);
        }
        return breadcrumb;
    }

    public static String slugify(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return normalized.isEmpty() ? "" : normalized;
    }
}
