package com.example.onlineshop.controller;

import com.example.onlineshop.model.Image;
import com.example.onlineshop.dto.ImageDTO;
import com.example.onlineshop.dto.ImageUpdateDTO;
import com.example.onlineshop.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

        @Operation(summary = "Upload an image to an admin product")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image uploaded successfully."),
                        @ApiResponse(responseCode = "400", description = "Invalid image file."),
                        @ApiResponse(responseCode = "403", description = "Forbidden."),
                        @ApiResponse(responseCode = "404", description = "Product not found.")
        })
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
        @PostMapping("/api/v1/admin/products/{productId}/images")
        public ResponseEntity<ImageDTO> uploadAdminProductImage(
                        @PathVariable Long productId,
                        @RequestParam("file") MultipartFile file) throws IOException {
                return ResponseEntity.ok(new ImageDTO(imageService.saveProductImage(productId, file)));
        }

        @Operation(summary = "Update product image display metadata")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image metadata updated."),
                        @ApiResponse(responseCode = "400", description = "Invalid metadata."),
                        @ApiResponse(responseCode = "403", description = "Forbidden."),
                        @ApiResponse(responseCode = "404", description = "Product image not found.")
        })
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
        @PatchMapping("/api/v1/admin/products/{productId}/images/{imageId}")
        public ResponseEntity<ImageDTO> patchAdminProductImage(
                        @PathVariable Long productId,
                        @PathVariable Long imageId,
                        @RequestBody ImageUpdateDTO update) {
                return ResponseEntity.ok(new ImageDTO(imageService.updateProductImage(productId, imageId, update)));
        }

        @Operation(summary = "Delete an admin product image")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Image deleted."),
                        @ApiResponse(responseCode = "403", description = "Forbidden."),
                        @ApiResponse(responseCode = "404", description = "Product image not found.")
        })
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
        @DeleteMapping("/api/v1/admin/products/{productId}/images/{imageId}")
        public ResponseEntity<Void> deleteAdminProductImage(
                        @PathVariable Long productId,
                        @PathVariable Long imageId) {
                imageService.deleteProductImage(productId, imageId);
                return ResponseEntity.noContent().build();
        }

    // Endpoint to upload an image
    @Operation(summary = "Upload Product Image", description = "Upload an image for a product with its name and type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid image file or data.")
    })
        @PostMapping("/api/v1/images/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("imageType") String imageType) throws IOException {

        Image image = imageService.saveImage(file, imageName, imageType);
        return ResponseEntity.ok("Image uploaded successfully with ID: " + image.getImageId());
    }

    // Endpoint to get an image by ID
    @Operation(summary = "Retrieve Product Image", description = "Retrieve a product image by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product image retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Image not found.")
    })
        @GetMapping("/api/v1/images/{imageId}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable Long imageId) {
        Image image = imageService.getImage(imageId);

        // Set up the response with the image data
        ByteArrayResource resource = new ByteArrayResource(image.getImageData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + image.getImageName())
                .body(resource);
    }

    // Endpoint to delete an image by ID
    @Operation(summary = "Delete Product Image", description = "Delete a product image by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Image not found.")
    })
        @DeleteMapping("/api/v1/images/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }

    // Endpoint to update a product image
    @Operation(summary = "Update Product Image", description = "Update a product's image by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully."),
            @ApiResponse(responseCode = "404", description = "Image not found."),
            @ApiResponse(responseCode = "400", description = "Invalid image file or data.")
    })
        @PutMapping("/api/v1/images/{imageId}/update")
    public ResponseEntity<String> updateProductImage(@PathVariable Long imageId,
                                                     @RequestParam("file") MultipartFile newImageFile) throws IOException {

        imageService.updateImage(imageId, newImageFile);
        return ResponseEntity.ok("Product image updated successfully.");
    }
}
