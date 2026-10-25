package com.example.onlineshop.content.controller;

import com.example.onlineshop.content.dto.*;
import com.example.onlineshop.common.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin content", description = "Administrative banners and featured collections")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminStaticContentController {
    private final AdminService adminService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:read')")
    @GetMapping("/banners")
    public List<BannerDTO> getBanners() { return adminService.getAllBannersForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:create')")
    @PostMapping("/banners")
    public BannerDTO createBanner(@RequestBody BannerDTO bannerDTO) { return adminService.createBannerForAdmin(bannerDTO); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:update')")
    @PatchMapping("/banners/{bannerId}")
    public BannerDTO patchBanner(@PathVariable Long bannerId, @RequestBody BannerUpdateDTO patch) { return adminService.updateBannerForAdmin(bannerId, patch); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:delete')")
    @DeleteMapping("/banners/{bannerId}")
    public ResponseEntity<Void> archiveBanner(@PathVariable Long bannerId) { adminService.archiveBannerForAdmin(bannerId); return ResponseEntity.noContent().build(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:read')")
    @GetMapping("/collections")
    public List<FeaturedCollectionDTO> getCollections() { return adminService.getAllCollectionsForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:create')")
    @PostMapping("/collections")
    public FeaturedCollectionDTO createCollection(@RequestBody FeaturedCollectionDTO collectionDTO) { return adminService.createCollectionForAdmin(collectionDTO); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:update')")
    @PatchMapping("/collections/{collectionId}")
    public FeaturedCollectionDTO patchCollection(@PathVariable Long collectionId, @RequestBody FeaturedCollectionUpdateDTO patch) { return adminService.updateCollectionForAdmin(collectionId, patch); }
}
