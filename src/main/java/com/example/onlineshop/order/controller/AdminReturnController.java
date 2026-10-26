package com.example.onlineshop.order.controller;

import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.order.dto.ReturnRequestDTO;
import com.example.onlineshop.order.dto.ReturnDecisionRequest;
import com.example.onlineshop.order.dto.ReturnInspectionRequest;
import com.example.onlineshop.order.dto.ReturnResolveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin returns", description = "Staff return-request operations")
@RestController
@RequestMapping("/api/v1/admin/returns")
@RequiredArgsConstructor
public class AdminReturnController {
    private final AdminService adminService;

    @Operation(summary = "List return requests", description = "List customer returns with optional status and eligibility filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns listed."),
            @ApiResponse(responseCode = "403", description = "Staff permission required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read')")
    @GetMapping
    public List<ReturnRequestDTO> getReturns(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean eligible) {
        return adminService.getAdminReturns(status, eligible);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{returnId}/approve")
    public ReturnRequestDTO approve(@PathVariable Long returnId, @RequestBody ReturnDecisionRequest request) {
        return adminService.approveReturn(returnId, request == null ? null : request.getReason());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{returnId}/reject")
    public ReturnRequestDTO reject(@PathVariable Long returnId, @RequestBody ReturnDecisionRequest request) {
        return adminService.rejectReturn(returnId, request == null ? null : request.getReason());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{returnId}/receive")
    public ReturnRequestDTO receive(@PathVariable Long returnId, @RequestBody ReturnInspectionRequest request) {
        return adminService.receiveReturn(returnId, request == null ? null : request.getInspectionNotes());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{returnId}/resolve")
    public ReturnRequestDTO resolve(@PathVariable Long returnId, @RequestBody ReturnResolveRequest request) {
        return adminService.resolveReturn(returnId, request == null ? null : request.getRefundDecision(),
                request == null ? null : request.getRefundAmount(), request == null ? null : request.getReason());
    }

}
