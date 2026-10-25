package com.example.onlineshop.product.controller;

import com.example.onlineshop.product.dto.*;
import com.example.onlineshop.promotion.dto.*;
import com.example.onlineshop.shipping.dto.*;
import com.example.onlineshop.common.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;

@Tag(name = "Admin products", description = "Administrative catalog and inventory management")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminProductController {
    private final AdminService adminService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:create')")
    @PostMapping("/products")
    public String addProduct(@RequestBody ProductDTO productDTO) { adminService.addProduct(productDTO); return "Product added successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:update')")
    @PutMapping("/products/{productId}")
    public void updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) { adminService.updateProduct(productId, productDTO); }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:delete')")
    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) { adminService.deleteProduct(productId); }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:stock')")
    @PatchMapping("/products/{productId}/stock")
    public String updateStock(@PathVariable Long productId, @RequestParam Long inventoryId, @RequestBody StockUpdateDTO stockUpdate) {
        adminService.updateStock(inventoryId, stockUpdate.getStock()); return "Stock updated successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:update')")
    @PatchMapping("/products/{productId}/inventory/{inventoryId}/pricing")
    public String updatePricing(@PathVariable Long productId, @PathVariable Long inventoryId, @RequestBody Map<String, Double> prices) {
        adminService.updateProductPricing(inventoryId, prices.getOrDefault("sellingPrice", 0d), prices.getOrDefault("costPrice", 0d));
        return "Pricing updated successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:stock')")
    @PostMapping("/products/inventory/{skuId}")
    public String addInventory(@PathVariable Long skuId, @RequestBody InventoryDTO inventoryDTO) {
        adminService.addInventory(skuId, inventoryDTO); return "Inventory added successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:approve')")
    @PutMapping("/products/{productId}/approval")
    public String approveProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId); adminService.approveProduct(productDTO); return "Product approved successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:approve')")
    @DeleteMapping("/products/{productId}/approval")
    public String rejectProduct(@PathVariable Long productId) { adminService.rejectProduct(productId); return "Product rejected successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:read')")
    @GetMapping("/products")
    public List<ProductDTO> getAdminProducts() { return adminService.getAllProductsForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:read')")
    @GetMapping("/products/{productId}")
    public ProductDTO getAdminProductById(@PathVariable Long productId) { return adminService.getProductForAdmin(productId); }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:update')")
    @PatchMapping("/products/{productId}")
    public ProductDTO patchAdminProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        return adminService.updateProductForAdmin(productId, productDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('product:approve')")
    @PatchMapping("/products/{productId}/status")
    public ProductDTO updateAdminProductStatus(@PathVariable Long productId, @RequestBody Map<String, String> payload) {
        return adminService.updateProductStatusForAdmin(productId, payload == null ? null : payload.get("status"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:read')")
    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() { return adminService.getAllCategoriesForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:read')")
    @GetMapping("/brands")
    public List<BrandDTO> getBrands() { return adminService.getAllBrandsForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:create')")
    @PostMapping("/brands")
    public BrandDTO createBrand(@RequestBody BrandDTO dto) { return adminService.createBrandForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:update')")
    @PatchMapping("/brands/{brandId}")
    public BrandDTO patchBrand(@PathVariable Long brandId, @RequestBody BrandDTO dto) { return adminService.updateBrandForAdmin(brandId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:read')")
    @GetMapping("/coupons")
    public List<CouponDTO> getCoupons(@RequestParam(required = false) String status, @RequestParam(required = false) String search, @RequestParam(required = false) String sort) { return adminService.getCouponsForAdmin(status, search, sort); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:create')")
    @PostMapping("/coupons")
    public CouponDTO createCoupon(@RequestBody CouponDTO dto) { return adminService.createCouponForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:update')")
    @PatchMapping("/coupons/{couponId}")
    public CouponDTO patchCoupon(@PathVariable Long couponId, @RequestBody CouponDTO dto) { return adminService.updateCouponForAdmin(couponId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:update')")
    @PatchMapping("/coupons/{couponId}/status")
    public CouponDTO updateCouponStatus(@PathVariable Long couponId, @RequestBody Map<String, String> payload) { return adminService.updateCouponStatusForAdmin(couponId, payload == null ? null : payload.get("status")); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:read')")
    @GetMapping("/tax-rules")
    public List<TaxRuleDTO> getTaxRules(@RequestParam(required = false) String region, @RequestParam(required = false) LocalDateTime effectiveAt) { return adminService.getTaxRulesForAdmin(region, effectiveAt); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:create')")
    @PostMapping("/tax-rules")
    public TaxRuleDTO createTaxRule(@Valid @RequestBody TaxRuleDTO dto) { return adminService.createTaxRuleForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:update')")
    @PatchMapping("/tax-rules/{ruleId}")
    public TaxRuleDTO patchTaxRule(@PathVariable Long ruleId, @RequestBody TaxRuleDTO dto) { return adminService.updateTaxRuleForAdmin(ruleId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:read')")
    @GetMapping("/shipping-zones")
    public List<ShippingZoneDTO> getShippingZones() { return adminService.getShippingZonesForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:create')")
    @PostMapping("/shipping-zones")
    public ShippingZoneDTO createShippingZone(@RequestBody ShippingZoneDTO dto) { return adminService.createShippingZoneForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:update')")
    @PatchMapping("/shipping-zones/{zoneId}")
    public ShippingZoneDTO patchShippingZone(@PathVariable Long zoneId, @RequestBody ShippingZoneDTO dto) { return adminService.updateShippingZoneForAdmin(zoneId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:create')")
    @PostMapping("/shipping-methods")
    public AdminShippingMethodDTO createShippingMethod(@RequestBody AdminShippingMethodDTO dto) { return adminService.createShippingMethodForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:update')")
    @PatchMapping("/shipping-methods/{methodId}")
    public AdminShippingMethodDTO patchShippingMethod(@PathVariable Long methodId, @RequestBody AdminShippingMethodDTO dto) { return adminService.updateShippingMethodForAdmin(methodId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    @GetMapping("/inventory")
    public List<AdminInventoryDTO> getInventory() { return adminService.getAdminInventory(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    @GetMapping("/inventory/{variantId}/movements")
    public Page<InventoryMovementDTO> getMovements(@PathVariable Long variantId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) { return adminService.getInventoryMovements(variantId, page, size); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:adjust')")
    @PostMapping("/inventory/{variantId}/adjustments")
    public InventoryDTO adjustInventory(@PathVariable Long variantId, @Valid @RequestBody InventoryAdjustmentDTO dto) { return adminService.adjustInventory(variantId, dto.getQuantityDelta(), dto.getReason(), dto.getReference()); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:update')")
    @PatchMapping("/inventory/{variantId}/threshold")
    public InventoryDTO setThreshold(@PathVariable Long variantId, @Valid @RequestBody InventoryThresholdDTO dto) { return adminService.setInventoryThreshold(variantId, dto.getLowStockThreshold()); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    @GetMapping("/inventory/low-stock")
    public Page<AdminInventoryDTO> getLowStock(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) { return adminService.getLowStockInventory(page, size); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:read')")
    @GetMapping("/attributes")
    public List<CategoryAttributeDTO> getAttributes() { return adminService.getAllCategoryAttributesForAdmin(); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:create')")
    @PostMapping("/attributes")
    public CategoryAttributeDTO createAttribute(@RequestBody CategoryAttributeDTO dto) { return adminService.createCategoryAttributeForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:update')")
    @PatchMapping("/attributes/{attributeId}")
    public CategoryAttributeDTO patchAttribute(@PathVariable Long attributeId, @RequestBody CategoryAttributeDTO dto) { return adminService.updateCategoryAttributeForAdmin(attributeId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:create')")
    @PostMapping("/products/{productId}/variants")
    public SKUDTO createVariant(@PathVariable Long productId, @Valid @RequestBody VariantCreateDTO dto) { return adminService.createVariantForAdmin(productId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    @PatchMapping("/products/{productId}/variants/{variantId}")
    public SKUDTO patchVariant(@PathVariable Long productId, @PathVariable Long variantId, @RequestBody VariantUpdateDTO dto) { return adminService.updateVariantForAdmin(productId, variantId, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    @PatchMapping("/products/{productId}/variants/{variantId}/status")
    public SKUDTO patchVariantStatus(@PathVariable Long productId, @PathVariable Long variantId, @RequestBody Map<String, String> payload) { return adminService.updateVariantStatusForAdmin(productId, variantId, payload == null ? null : payload.get("status")); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:create')")
    @PostMapping("/categories")
    public CategoryDTO createCategory(@RequestBody CategoryDTO dto) { return adminService.createCategoryForAdmin(dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:update')")
    @PatchMapping({"/categories/{category}", "/categories/{category}/{subcategory}"})
    public CategoryDTO patchCategory(@PathVariable String category, @PathVariable(required = false) String subcategory, @RequestBody CategoryDTO dto) { return adminService.updateCategoryForAdmin(category, subcategory, dto); }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:delete')")
    @DeleteMapping({"/categories/{category}", "/categories/{category}/{subcategory}"})
    public String deleteCategory(@PathVariable String category, @PathVariable(required = false) String subcategory) { adminService.deleteCategoryForAdmin(category, subcategory); return "Category deleted successfully."; }
}
