package com.example.onlineshop.product.controller;

import com.example.onlineshop.product.dto.ProductDTO;
import com.example.onlineshop.product.dto.StockUpdateDTO;
import com.example.onlineshop.product.dto.InventoryDTO;
import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products", description = "Catalog browsing and product management")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Get all products", description = "Retrieve a paginated, filterable public product listing for the storefront.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @Cacheable(value = "all_products")
    @GetMapping
    public List<ProductDTO> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "featured") String sort) {
        return buyerService.getProducts(category, brand, minPrice, maxPrice, available, sort);
    }

    @Operation(summary = "Search products", description = "Search products based on a keyword.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword.")
    })
    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam String keyword) {
        return buyerService.searchProducts(keyword);
    }

    @Operation(summary = "Get related products for a product detail page", description = "Return the same-category active products as a lightweight recommendation list.")
    @GetMapping("/{productId}/related")
    public List<ProductDTO> getRelatedProducts(@PathVariable Long productId) {
        return buyerService.getRelatedProducts(productId);
    }

    @Operation(summary = "Get public product reviews", description = "Return approved/public reviews for a product with count and average metadata.")
    @GetMapping("/{productId}/reviews")
    public List<com.example.onlineshop.product.entity.Review> getProductReviews(@PathVariable Long productId) {
        return buyerService.getProductReviews(productId);
    }

    @Operation(summary = "Suggest matching products as users type", description = "Return a short list of product names for the keyword suggestion box.")
    @GetMapping("/search/suggestions")
    public List<String> searchSuggestions(@RequestParam String query) {
        return buyerService.getSearchSuggestions(query);
    }

    @Operation(summary = "Filter products by category and keyword", description = "Filter products by category and subcategory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters.")
    })
    @GetMapping("/filter")
    public List<ProductDTO> filterProducts(
            @RequestParam String category,
            @RequestParam String keyword) {
        return buyerService.filterProducts(category, keyword);
    }

    @Operation(summary = "Get product by ID or slug", description = "Retrieve a public product by ID or a URL-safe product slug.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Product not found.")
    })
    @GetMapping("/{productIdOrSlug}")
    public ProductDTO getProductById(@PathVariable String productIdOrSlug) {
        return buyerService.getProductByIdOrSlug(productIdOrSlug);
    }

    @Operation(summary = "Get products by category", description = "Retrieve products based on their category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products by category retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No products found for this category.")
    })
    @GetMapping("/category/{category}")
    public List<ProductDTO> getProductsByCategory(@PathVariable String category) {
        return buyerService.getProductsByCategory(category);
    }

    @Operation(summary = "Get products by subcategory", description = "Retrieve products based on their subcategory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products by subcategory retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No products found for this subcategory.")
    })
    @GetMapping("/subcategory/{subcategory}")
    public List<ProductDTO> getProductsBySubcategory(@PathVariable String subcategory) {
        return buyerService.getProductsBySubcategory(subcategory);
    }

}
