package com.example.onlineshop.repository;

import com.example.onlineshop.model.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRuleRepository extends JpaRepository<TaxRule, Long> {
    List<TaxRule> findAllByOrderByRegionAscEffectiveFromDesc();

    List<TaxRule> findByRegionIgnoreCaseAndActiveTrue(String region);
}