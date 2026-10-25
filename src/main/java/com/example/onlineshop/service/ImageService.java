package com.example.onlineshop.service;

import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.repository.ImageRepository;
import com.example.onlineshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    public Image saveProductImage(Long productId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be non-empty and no larger than 5 MiB");
        }
        byte[] content = file.getBytes();
        String contentType = detectImageType(content);
        if (contentType == null) {
            throw new IllegalArgumentException("Only valid JPEG, PNG, GIF, or WebP images are accepted");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String safeName = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        if (safeName.isBlank() || safeName.equals(".") || safeName.equals("..")) {
            safeName = "image";
        }

        Image image = new Image();
        image.setImageData(content);
        image.setImageName(safeName);
        image.setImageType(contentType);
        image.setProduct(product);
        return imageRepository.save(image);
    }

    @Transactional
    public Image updateProductImage(Long productId, Long imageId, com.example.onlineshop.dto.ImageUpdateDTO update) {
        if (update == null) {
            throw new IllegalArgumentException("Image update is required");
        }
        Image image = imageRepository.findByImageIdAndProduct_ProductId(imageId, productId)
                .orElseThrow(() -> new RuntimeException("Product image not found"));
        if (update.getAltText() != null) {
            if (update.getAltText().length() > 255) {
                throw new IllegalArgumentException("Alt text must not exceed 255 characters");
            }
            image.setAltText(update.getAltText().trim());
        }
        if (update.getDisplayOrder() != null) {
            if (update.getDisplayOrder() < 0) {
                throw new IllegalArgumentException("Display order cannot be negative");
            }
            image.setDisplayOrder(update.getDisplayOrder());
        }
        if (Boolean.TRUE.equals(update.getPrimaryImage())) {
            for (Image sibling : imageRepository.findByProduct_ProductIdOrderByDisplayOrderAscImageIdAsc(productId)) {
                if (!sibling.getImageId().equals(imageId) && sibling.isPrimaryImage()) {
                    sibling.setPrimaryImage(false);
                    imageRepository.save(sibling);
                }
            }
            image.setPrimaryImage(true);
        } else if (Boolean.FALSE.equals(update.getPrimaryImage())) {
            image.setPrimaryImage(false);
        }
        return imageRepository.save(image);
    }

    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        Image image = imageRepository.findByImageIdAndProduct_ProductId(imageId, productId)
                .orElseThrow(() -> new RuntimeException("Product image not found"));

        if (image.isPrimaryImage()) {
            imageRepository.findByProduct_ProductIdOrderByDisplayOrderAscImageIdAsc(productId).stream()
                    .filter(candidate -> !candidate.getImageId().equals(imageId))
                    .findFirst()
                    .ifPresent(candidate -> {
                        candidate.setPrimaryImage(true);
                        imageRepository.save(candidate);
                    });
        }

        imageRepository.delete(image);
    }

    private String detectImageType(byte[] content) {
        if (content.length >= 8 && (content[0] & 0xff) == 0x89 && content[1] == 'P'
                && content[2] == 'N' && content[3] == 'G' && content[4] == 0x0d
                && content[5] == 0x0a && content[6] == 0x1a && content[7] == 0x0a) {
            return "image/png";
        }
        if (content.length >= 3 && (content[0] & 0xff) == 0xff && (content[1] & 0xff) == 0xd8
                && (content[2] & 0xff) == 0xff) {
            return "image/jpeg";
        }
        if (content.length >= 6 && new String(content, 0, 3, java.nio.charset.StandardCharsets.US_ASCII).equals("GIF")) {
            return "image/gif";
        }
        if (content.length >= 12 && new String(content, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
                && new String(content, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP")) {
            return "image/webp";
        }
        return null;
    }

    // Save an image to the database
    public Image saveImage(MultipartFile file, String imageName, String imageType) throws IOException {
        Image image = new Image();
        image.setImageData(file.getBytes());  // Convert the file to byte array
        image.setImageName(imageName);        // Set the image name
        image.setImageType(imageType);        // Set the image type (MIME type)

        return imageRepository.save(image);
    }

    // Get an image by its ID
    public Image getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    // Delete an image by its ID
    public void deleteImage(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found with id: " + imageId);
        }
        imageRepository.deleteById(imageId);
    }

    // Update an existing image
    public Image updateImage(Long imageId, MultipartFile newFile) throws IOException {
        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        existingImage.setImageData(newFile.getBytes());  // Update image data

        return imageRepository.save(existingImage);
    }
}
