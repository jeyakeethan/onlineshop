package com.example.onlineshop.report.controller;

import com.example.onlineshop.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Reports", description = "Analytics and administrative metrics")
@RestController
@RequestMapping("/api/v1/admin/reports")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "Get sales report", description = "Retrieve a report on sales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sales report."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('report:read')")
    @GetMapping("/sales")
    public String getSalesReport() {
        return reportService.getSalesReport();
    }

    @Operation(summary = "Get user statistics", description = "Retrieve user statistics.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user statistics."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('report:read')")
    @GetMapping("/users")
    public String getUserStatistics() {
        return reportService.getUserStatistics();
    }

    @Operation(summary = "Get product statistics", description = "Retrieve statistics about products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product statistics."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('report:read')")
    @GetMapping("/products")
    public String getProductStats() {
        return reportService.getProductStats();
    }

    @Operation(summary = "Download order report", description = "Download a bounded CSV order report for a date range.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('report:read')")
    @GetMapping(value = "/orders", produces = "text/csv")
    public ResponseEntity<String> getOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        String csv = reportService.getOrderReportCsv(from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @Operation(summary = "Download inventory report", description = "Download a bounded CSV inventory report.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('report:read')")
    @GetMapping(value = "/inventory", produces = "text/csv")
    public ResponseEntity<String> getInventoryReport() {
        String csv = reportService.getInventoryReportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
