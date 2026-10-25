package com.example.onlineshop.promotion.repository;

import com.example.onlineshop.promotion.entity.SeasonalDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SeasonalDiscountRepository extends JpaRepository<SeasonalDiscount, Long> {
}
