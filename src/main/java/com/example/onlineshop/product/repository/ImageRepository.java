package com.example.onlineshop.product.repository;

import com.example.onlineshop.product.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageIdAndProduct_ProductId(Long imageId, Long productId);

    List<Image> findByProduct_ProductIdOrderByDisplayOrderAscImageIdAsc(Long productId);
}
