package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.RefundExecutionDTO;
import com.example.onlineshop.order.dto.RefundExecutionRequest;
import com.example.onlineshop.order.service.RefundExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin refunds", description = "Permission-gated refund execution")
@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
public class AdminRefundController {
    private final RefundExecutionService refundExecutionService;

    @Operation(summary = "Execute an approved refund", description = "Execute a resolved refund through the configured provider with idempotency protection.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund executed or replayed."),
            @ApiResponse(responseCode = "400", description = "Refund is invalid or not eligible."),
            @ApiResponse(responseCode = "403", description = "Refund execution permission required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('refund:execute')")
    @PostMapping("/{refundId}/execute")
    public RefundExecutionDTO executeRefund(
            @PathVariable Long refundId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody(required = false) RefundExecutionRequest request) {
        return refundExecutionService.execute(refundId, request, idempotencyKey);
    }
}
