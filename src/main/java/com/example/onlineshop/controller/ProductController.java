package com.example.onlineshop.controller;

import com.example.onlineshop.dto.ProductDTO;
import com.example.onlineshop.dto.StockUpdateDTO;
import com.example.onlineshop.dto.InventoryDTO;
import com.example.onlineshop.service.AdminService;
import com.example.onlineshop.service.BuyerService;
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
    public List<com.example.onlineshop.model.Review> getProductReviews(@PathVariable Long productId) {
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

    @Operation(summary = "Create a new product", description = "Add a new product to the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid product data."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:create')")
    @PostMapping
    public String addProduct(@RequestBody ProductDTO productDTO) {
        adminService.addProduct(productDTO);
        return "Product added successfully.";
    }

    @Operation(summary = "Update a product", description = "Update the details of an existing product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid product data."),
            @ApiResponse(responseCode = "404", description = "Product not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:update')")
    @PutMapping("/{productId}")
    public void updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        adminService.updateProduct(productId, productDTO);
    }

    @Operation(summary = "Delete a product", description = "Delete an existing product from the store.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Product not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:delete')")
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        adminService.deleteProduct(productId);
    }

    @Operation(summary = "Update product stock", description = "Update the stock for a specific product inventory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully."),
            @ApiResponse(responseCode = "404", description = "Inventory not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:stock')")
    @PatchMapping("/{productId}/stock")
    public String updateStock(
            @PathVariable Long productId,
            @RequestParam Long inventoryId,
            @RequestBody StockUpdateDTO stockUpdate) {
        adminService.updateStock(inventoryId, stockUpdate.getStock());
        return "Stock updated successfully.";
    }

        @Operation(summary = "Update inventory pricing", description = "Update cost and selling prices for a specific inventory record.")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:update')")
        @PatchMapping("/{productId}/inventory/{inventoryId}/pricing")
        public String updatePricing(
                        @PathVariable Long productId,
                        @PathVariable Long inventoryId,
                        @RequestBody java.util.Map<String, Double> prices) {
                adminService.updateProductPricing(
                                inventoryId,
                                prices.getOrDefault("sellingPrice", 0d),
                                prices.getOrDefault("costPrice", 0d));
                return "Pricing updated successfully.";
        }

        @Operation(summary = "Add product inventory", description = "Add an inventory record for a product SKU.")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:stock')")
        @PostMapping("/inventory/{skuId}")
        public String addInventory(@PathVariable Long skuId, @RequestBody InventoryDTO inventoryDTO) {
                adminService.addInventory(skuId, inventoryDTO);
                return "Inventory added successfully.";
        }

    @Operation(summary = "Approve a product", description = "Approve a product after review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product approved successfully."),
            @ApiResponse(responseCode = "404", description = "Product not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:approve')")
    @PutMapping("/{productId}/approval")
    public String approveProduct(
            @PathVariable Long productId,
            @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId);
        adminService.approveProduct(productDTO);
        return "Product approved successfully.";
    }

    @Operation(summary = "Reject a product", description = "Reject a product after review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product rejected successfully."),
            @ApiResponse(responseCode = "404", description = "Product not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin access required.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:approve')")
    @DeleteMapping("/{productId}/approval")
    public String rejectProduct(@PathVariable Long productId) {
        adminService.rejectProduct(productId);
        return "Product rejected successfully.";
    }
}
