package com.example.onlineshop.content.repository;

import com.example.onlineshop.content.entity.FeaturedCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeaturedCollectionRepository extends JpaRepository<FeaturedCollection, Long> {
    List<FeaturedCollection> findAllByOrderByDisplayOrderAscCollectionIdAsc();

    boolean existsBySlug(String slug);
}
