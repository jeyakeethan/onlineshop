package com.example.onlineshop.product.controller;

import com.example.onlineshop.product.entity.Image;
import com.example.onlineshop.product.dto.ImageDTO;
import com.example.onlineshop.product.dto.ImageUpdateDTO;
import com.example.onlineshop.product.service.ImageService;
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
