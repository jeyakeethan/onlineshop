package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_subcategory", columnList = "subcategory")  // Add index
})
@IdClass(Category.CategoryPK.class) // Define composite primary key
@Getter
@Setter
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String category;

    @Id
    private String subcategory;

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class CategoryPK implements Serializable {
        private String category;
        private String subcategory;
    }
}
