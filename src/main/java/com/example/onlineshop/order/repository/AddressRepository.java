package com.example.onlineshop.order.repository;

import com.example.onlineshop.order.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser_Email(String userId);
}
