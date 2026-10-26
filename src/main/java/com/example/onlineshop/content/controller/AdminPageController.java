package com.example.onlineshop.content.controller;

import com.example.onlineshop.content.dto.StaticPageDTO;
import com.example.onlineshop.content.dto.AdminPageCreateDTO;
import com.example.onlineshop.content.dto.AdminPageUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.onlineshop.content.service.AdminPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/admin/pages") @RequiredArgsConstructor
public class AdminPageController {
    private final AdminPageService adminPageService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('content:read')")
    @GetMapping
    public Page<StaticPageDTO> getPages(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        return adminPageService.getPages(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('content:create')")
    @org.springframework.web.bind.annotation.PostMapping
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public StaticPageDTO createPage(@Valid @org.springframework.web.bind.annotation.RequestBody AdminPageCreateDTO request) {
        return adminPageService.createPage(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('content:update')")
    @org.springframework.web.bind.annotation.PatchMapping("/{pageId}")
    public StaticPageDTO updatePage(@org.springframework.web.bind.annotation.PathVariable Long pageId,
                                    @Valid @org.springframework.web.bind.annotation.RequestBody AdminPageUpdateDTO request) {
        return adminPageService.updatePage(pageId, request);
    }
}
