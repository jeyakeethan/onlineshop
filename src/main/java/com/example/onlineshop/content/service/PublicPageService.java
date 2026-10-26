package com.example.onlineshop.content.service;

import com.example.onlineshop.content.dto.StaticPageDTO;
import com.example.onlineshop.content.entity.StaticPage;
import com.example.onlineshop.content.repository.StaticPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class PublicPageService {
    private final StaticPageRepository staticPageRepository;

    public StaticPageDTO getPublishedPage(String slug) {
        if (slug == null || slug.isBlank() || slug.trim().length() > 160) {
            throw new IllegalArgumentException("A valid page slug is required");
        }
        StaticPage page = staticPageRepository.findBySlugIgnoreCase(slug.trim())
                .orElseThrow(() -> new IllegalArgumentException("Page not found"));
        if (!page.isPublished() || page.getPublishedAt() != null && page.getPublishedAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Page not found");
        }
        return new StaticPageDTO(page);
    }
}
