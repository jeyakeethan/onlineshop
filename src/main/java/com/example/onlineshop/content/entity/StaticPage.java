package com.example.onlineshop.content.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "static_pages")
@Getter @Setter @NoArgsConstructor
public class StaticPage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pageId;
    private String slug;
    private String title;
    @Lob private String content;
    private boolean published;
    private LocalDateTime publishedAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
