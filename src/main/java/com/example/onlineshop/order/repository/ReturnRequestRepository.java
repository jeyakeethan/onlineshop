package com.example.onlineshop.order.repository;

import com.example.onlineshop.order.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByOrder_OrderIdAndOrder_User_EmailIgnoreCaseOrderByRequestedAtDesc(Long orderId, String email);
    List<ReturnRequest> findByRefundStatus(String refundStatus);
}
