package com.example.onlineshop.product.controller;

import com.example.onlineshop.product.dto.ImageDTO;
import com.example.onlineshop.product.dto.ImageUpdateDTO;
import com.example.onlineshop.product.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Admin product images", description = "Administrative product image management")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductImageController {
    private final ImageService imageService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    @PostMapping("/{productId}/images")
    public ResponseEntity<ImageDTO> upload(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(new ImageDTO(imageService.saveProductImage(productId, file)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    @PatchMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ImageDTO> patch(@PathVariable Long productId, @PathVariable Long imageId, @RequestBody ImageUpdateDTO update) {
        return ResponseEntity.ok(new ImageDTO(imageService.updateProductImage(productId, imageId, update)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long imageId) {
        imageService.deleteProductImage(productId, imageId); return ResponseEntity.noContent().build();
    }
}
