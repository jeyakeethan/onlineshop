package com.example.onlineshop.content.controller;

import com.example.onlineshop.content.dto.StaticPageDTO;
import com.example.onlineshop.content.service.PublicPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/pages") @RequiredArgsConstructor
public class PublicPageController {
    private final PublicPageService publicPageService;

    @GetMapping("/{slug}")
    public StaticPageDTO getPage(@PathVariable String slug) {
        return publicPageService.getPublishedPage(slug);
    }
}
