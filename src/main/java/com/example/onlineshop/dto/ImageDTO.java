package com.example.onlineshop.dto;

import com.example.onlineshop.model.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDTO {
    private Long imageId;
    private Long productId;
    private String imageName;
    private String imageType;
    private String altText;
    private int displayOrder;
    private boolean primaryImage;

    public ImageDTO(Image image) {
        this.imageId = image.getImageId();
        this.productId = image.getProduct() == null ? null : image.getProduct().getProductId();
        this.imageName = image.getImageName();
        this.imageType = image.getImageType();
        this.altText = image.getAltText();
        this.displayOrder = image.getDisplayOrder();
        this.primaryImage = image.isPrimaryImage();
    }
}