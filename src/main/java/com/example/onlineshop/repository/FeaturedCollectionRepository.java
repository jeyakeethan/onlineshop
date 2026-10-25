package com.example.onlineshop.repository;

import com.example.onlineshop.model.FeaturedCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeaturedCollectionRepository extends JpaRepository<FeaturedCollection, Long> {
    List<FeaturedCollection> findAllByOrderByDisplayOrderAscCollectionIdAsc();

    boolean existsBySlug(String slug);
}