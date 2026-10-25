package com.example.onlineshop.content.repository;

import com.example.onlineshop.content.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByOrderByDisplayOrderAscBannerIdAsc();
}
