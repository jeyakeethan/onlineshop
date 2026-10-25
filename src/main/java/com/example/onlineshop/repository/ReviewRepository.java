package com.example.onlineshop.repository;

import com.example.onlineshop.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select review from Review review where review.product.productId = :productId")
    List<Review> findByProductId(@Param("productId") Long productId);
}
