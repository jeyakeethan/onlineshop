package com.example.onlineshop.dto;

import com.example.onlineshop.model.CategoryAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryAttributeDTO {

    private Long id;
    private String category;
    private String subcategory;
    private String name;
    private String dataType;
    private List<String> allowedValues;
    private Boolean filterable;

    public CategoryAttributeDTO(CategoryAttribute attribute) {
        this.id = attribute.getId();
        this.category = attribute.getCategory();
        this.subcategory = attribute.getSubcategory();
        this.name = attribute.getName();
        this.dataType = attribute.getDataType();
        this.allowedValues = attribute.getAllowedValues() == null ? List.of() : List.copyOf(attribute.getAllowedValues());
        this.filterable = attribute.isFilterable();
    }

    public boolean isFilterable() {
        return Boolean.TRUE.equals(filterable);
    }
}