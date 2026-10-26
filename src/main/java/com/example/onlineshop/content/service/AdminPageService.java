package com.example.onlineshop.content.service;

import com.example.onlineshop.content.dto.StaticPageDTO;
import com.example.onlineshop.content.dto.AdminPageCreateDTO;
import com.example.onlineshop.content.dto.AdminPageUpdateDTO;
import com.example.onlineshop.content.entity.StaticPage;
import com.example.onlineshop.content.repository.StaticPageRepository;
import com.example.onlineshop.common.security.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.util.Locale;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class AdminPageService {
    private final StaticPageRepository staticPageRepository;
    private final AuthenticationHelper authenticationHelper;

    public Page<StaticPageDTO> getPages(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        return staticPageRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "slug")))
                .map(StaticPageDTO::new);
    }

    @Transactional
    public StaticPageDTO createPage(AdminPageCreateDTO request) {
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()
                || request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Title and content are required");
        }
        String slug = normalizeSlug(request.getSlug() == null || request.getSlug().isBlank()
                ? request.getTitle() : request.getSlug());
        if (slug.isBlank() || staticPageRepository.existsBySlugIgnoreCase(slug)) {
            throw new IllegalArgumentException("Page slug must be unique and valid");
        }
        StaticPage page = new StaticPage();
        page.setSlug(slug);
        page.setTitle(request.getTitle().trim());
        page.setContent(sanitizeRichText(request.getContent()));
        page.setPublished(request.isPublished());
        page.setPublishedAt(request.isPublished() ? java.time.LocalDateTime.now() : null);
        page.setLastUpdatedBy(authenticationHelper.getCurrentUserId());
        page.setLastUpdatedAt(LocalDateTime.now());
        return new StaticPageDTO(staticPageRepository.save(page));
    }

    @Transactional
    public StaticPageDTO updatePage(Long pageId, AdminPageUpdateDTO request) {
        if (request == null || request.getSlug() == null && request.getTitle() == null
                && request.getContent() == null && request.getPublished() == null) {
            throw new IllegalArgumentException("At least one page field must be provided");
        }
        StaticPage page = staticPageRepository.findById(pageId)
                .orElseThrow(() -> new IllegalArgumentException("Page not found"));
        if (request.getSlug() != null) {
            String slug = normalizeSlug(request.getSlug());
            if (slug.isBlank()) throw new IllegalArgumentException("Page slug must be valid");
            StaticPage existing = staticPageRepository.findBySlugIgnoreCase(slug).orElse(null);
            if (existing != null && !pageId.equals(existing.getPageId())) {
                throw new IllegalArgumentException("Page slug must be unique");
            }
            page.setSlug(slug);
        }
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) throw new IllegalArgumentException("Page title cannot be blank");
            page.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            if (request.getContent().isBlank()) throw new IllegalArgumentException("Page content cannot be blank");
            page.setContent(sanitizeRichText(request.getContent()));
        }
        if (request.getPublished() != null) {
            page.setPublished(request.getPublished());
            page.setPublishedAt(request.getPublished() ? LocalDateTime.now() : null);
        }
        page.setLastUpdatedBy(authenticationHelper.getCurrentUserId());
        page.setLastUpdatedAt(LocalDateTime.now());
        return new StaticPageDTO(staticPageRepository.save(page));
    }

    static String normalizeSlug(String value) {
        String normalized = Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    static String sanitizeRichText(String content) {
        String withoutDangerousBlocks = content.replaceAll("(?is)<(script|style|iframe|object)[^>]*>.*?</\\1>", "");
        return withoutDangerousBlocks.replaceAll("<(/?)(p|br|strong|em|ul|ol|li|a)(?:\\s[^>]*)?>", "<$1$2>")
                .replaceAll("<(?!(?:/?(?:p|br|strong|em|ul|ol|li|a)>))[^>]*>", "");
    }
}
