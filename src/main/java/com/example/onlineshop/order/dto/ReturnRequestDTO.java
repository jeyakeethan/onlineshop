package com.example.onlineshop.order.dto;

import com.example.onlineshop.order.entity.ReturnRequest;
import com.example.onlineshop.order.entity.ReturnRequestItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReturnRequestDTO {
    private final Long returnRequestId;
    private final Long orderId;
    private final String status;
    private final String reason;
    private final LocalDateTime requestedAt;
    private final String decisionReason;
    private final LocalDateTime reviewedAt;
    private final String reviewedBy;
    private final String inspectionNotes;
    private final LocalDateTime receivedAt;
    private final String receivedBy;
    private final String refundDecision;
    private final double refundAmount;
    private final LocalDateTime resolvedAt;
    private final String resolvedBy;
    private final List<ReturnItemDTO> items;

    public ReturnRequestDTO(ReturnRequest request) {
        this.returnRequestId = request.getReturnRequestId();
        this.orderId = request.getOrder().getOrderId();
        this.status = request.getStatus();
        this.reason = request.getReason();
        this.requestedAt = request.getRequestedAt();
        this.decisionReason = request.getDecisionReason();
        this.reviewedAt = request.getReviewedAt();
        this.reviewedBy = request.getReviewedBy();
        this.inspectionNotes = request.getInspectionNotes();
        this.receivedAt = request.getReceivedAt();
        this.receivedBy = request.getReceivedBy();
        this.refundDecision = request.getRefundDecision();
        this.refundAmount = request.getRefundAmount();
        this.resolvedAt = request.getResolvedAt();
        this.resolvedBy = request.getResolvedBy();
        this.items = (request.getItems() == null ? List.<ReturnRequestItem>of() : request.getItems()).stream()
                .map(ReturnItemDTO::new).collect(Collectors.toList());
    }
}
