package com.example.onlineshop.content.repository;

import com.example.onlineshop.content.entity.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaticPageRepository extends JpaRepository<StaticPage, Long> {
    Optional<StaticPage> findBySlugIgnoreCase(String slug);
    boolean existsBySlugIgnoreCase(String slug);
}
