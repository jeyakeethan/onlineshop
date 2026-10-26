package com.example.onlineshop.content.dto;

import com.example.onlineshop.content.entity.StaticPage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class StaticPageDTO {
    private Long pageId;
    private String slug;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private boolean published;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;

    public StaticPageDTO(StaticPage page) {
        this.pageId = page.getPageId();
        this.slug = page.getSlug();
        this.title = page.getTitle();
        this.content = page.getContent();
        this.publishedAt = page.getPublishedAt();
        this.published = page.isPublished();
        this.lastUpdatedBy = page.getLastUpdatedBy();
        this.lastUpdatedAt = page.getLastUpdatedAt();
    }
}
