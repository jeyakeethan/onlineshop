package com.example.onlineshop.user.controller;

import com.example.onlineshop.order.dto.AddressDTO;
import com.example.onlineshop.product.dto.AdminInventoryDTO;
import com.example.onlineshop.product.dto.InventoryAdjustmentDTO;
import com.example.onlineshop.product.dto.InventoryDTO;
import com.example.onlineshop.product.dto.InventoryThresholdDTO;
import com.example.onlineshop.product.dto.InventoryMovementDTO;
import com.example.onlineshop.user.dto.AuthenticationRequest;
import com.example.onlineshop.user.dto.AuthenticationResponse;
import com.example.onlineshop.product.dto.BrandDTO;
import com.example.onlineshop.content.dto.BannerDTO;
import com.example.onlineshop.content.dto.BannerUpdateDTO;
import com.example.onlineshop.content.dto.FeaturedCollectionDTO;
import com.example.onlineshop.content.dto.FeaturedCollectionUpdateDTO;
import com.example.onlineshop.product.dto.CategoryDTO;
import com.example.onlineshop.product.dto.CategoryAttributeDTO;
import com.example.onlineshop.promotion.dto.CouponDTO;
import com.example.onlineshop.shipping.dto.TaxRuleDTO;
import com.example.onlineshop.shipping.dto.ShippingZoneDTO;
import com.example.onlineshop.shipping.dto.AdminShippingMethodDTO;
import com.example.onlineshop.product.dto.SKUDTO;
import com.example.onlineshop.product.dto.VariantCreateDTO;
import com.example.onlineshop.product.dto.VariantUpdateDTO;
import com.example.onlineshop.user.dto.ForgotPasswordRequest;
import com.example.onlineshop.user.dto.ForgotPasswordResponse;
import com.example.onlineshop.product.dto.ProductDTO;
import com.example.onlineshop.user.dto.ResetPasswordRequest;
import com.example.onlineshop.user.dto.ResetPasswordResponse;
import com.example.onlineshop.user.dto.RoleDTO;
import com.example.onlineshop.user.dto.RegistrationRequest;
import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.user.dto.VerificationResendRequest;
import com.example.onlineshop.user.dto.VerifyEmailRequest;
import com.example.onlineshop.order.entity.Address;
import com.example.onlineshop.security.JwtUtil;
import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.common.service.BuyerService;
import com.example.onlineshop.user.service.RoleService;
import com.example.onlineshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Map;
import java.time.LocalDateTime;

@Tag(name = "Users & Auth", description = "Authentication, profile, user management, and role assignments")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- Authentication ---

    @Operation(summary = "Register a new user with normalized email and email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration accepted and email verification required."),
            @ApiResponse(responseCode = "400", description = "Invalid request or weak password."),
            @ApiResponse(responseCode = "409", description = "Email already registered.")
    })
    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/api/v1/auth/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        return new AuthenticationResponse(userService.authenticateUser(request.getUserId(), request.getPassword()));
    }

    @Operation(summary = "Logout the current session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "400", description = "Missing or invalid Authorization header.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Bearer token is required.");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isBlank()) {
            throw new IllegalArgumentException("Bearer token is required.");
        }

        userService.revokeToken(token);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @Operation(summary = "Refresh an active JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fresh token returned."),
            @ApiResponse(responseCode = "401", description = "Invalid or expired session.")
    })
    @PostMapping("/api/v1/auth/refresh")
    public AuthenticationResponse refresh(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh token is required.");
        }

        String token = authorizationHeader.substring(7);
        String userId = jwtUtil.extractUsername(token);
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Refresh token is invalid.");
        }

        return new AuthenticationResponse(userService.renewToken(userId));
    }

    @Operation(summary = "Request password reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dummy reset email prepared."),
            @ApiResponse(responseCode = "400", description = "Invalid email.")
    })
    @PostMapping("/api/v1/auth/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = "dummy-reset-token-" + System.currentTimeMillis();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        return ResponseEntity.ok(new ForgotPasswordResponse(
                "Password reset email template prepared. Email delivery is pending implementation.",
                "success",
                token,
                "Reset your password",
                "Hello,\n\nUse the following token to reset your password: " + token + "\n\nThis is a dummy response until the email service is connected."
        ));
    }

    @Operation(summary = "Reset a password using a reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dummy password reset response."),
            @ApiResponse(responseCode = "400", description = "Invalid reset request.")
    })
    @PostMapping("/api/v1/auth/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(new ResetPasswordResponse(
                "Password reset completed successfully (dummy response).",
                "success"
        ));
    }

    @Operation(summary = "Verify email address with token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token.")
    })
    @PostMapping("/api/v1/auth/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(userService.verifyEmail(request.getEmail(), request.getToken()));
    }

    @Operation(summary = "Resend verification email with a safe cooldown and generic response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generic success response; no account enumeration."),
            @ApiResponse(responseCode = "400", description = "Invalid email payload.")
    })
    @PostMapping("/api/v1/auth/verification/resend")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody VerificationResendRequest request) {
        String message = userService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Start Google login flow")
    @GetMapping("/api/v1/auth/google/start")
    public ResponseEntity<String> googleStart() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Google OAuth is not configured yet. Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET to enable login.");
    }

    @Operation(summary = "Handle Google callback")
    @GetMapping("/api/v1/auth/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam(required = false) String code, @RequestParam(required = false) String error) {
        if (error != null) {
            return ResponseEntity.badRequest().body("Google authentication was denied or failed.");
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Google OAuth callback is not configured yet. Add client credentials and callback handling.");
    }

    // --- Profile (current user) ---

    @Operation(summary = "Get current user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/v1/me")
    public UserDTO getCurrentUserProfile() {
        return buyerService.getUserProfile();
    }

    @Operation(summary = "Update current user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/api/v1/me")
    public void updateCurrentUserProfile(@Valid @RequestBody UserDTO userDTO) {
        buyerService.updateUserProfile(userDTO);
    }

    @Operation(summary = "Request account deactivation")
    @PreAuthorize("isAuthenticated()")
    @PostMapping({"/api/v1/me/deactivation-request", "/api/v1/me/deactivate"})
    public ResponseEntity<String> deactivateCurrentAccount() {
        return ResponseEntity.ok(buyerService.requestAccountDeactivation());
    }

    @Operation(summary = "Request account deletion and retention-based anonymization")
    @PreAuthorize("isAuthenticated()")
    @PostMapping({"/api/v1/me/deletion-request", "/api/v1/me/delete"})
    public ResponseEntity<String> requestDeletion() {
        return ResponseEntity.ok(buyerService.requestAccountDeletion());
    }

    @Operation(summary = "List all addresses for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user addresses."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/v1/me/addresses")
    public List<AddressDTO> getCurrentUserAddresses() {
        return buyerService.getUserAddresses();
    }

    @Operation(summary = "Create a new address for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid address payload."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/me/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO addressDTO) {
        Address address = buyerService.createAddress(addressDTO);
        return ResponseEntity.ok(new AddressDTO(address));
    }

    @Operation(summary = "Update one of the current user's addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid address payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden: address does not belong to the current user."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/api/v1/me/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        Address updated = buyerService.updateAddress(addressId, addressDTO);
        return ResponseEntity.ok(new AddressDTO(updated));
    }

    @Operation(summary = "Delete one of the current user's addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Forbidden: address does not belong to the current user."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/api/v1/me/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(buyerService.deleteAddress(addressId));
    }

    @Operation(summary = "Set one address as the user's default address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default address set successfully."),
            @ApiResponse(responseCode = "403", description = "Forbidden: address does not belong to the current user."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/api/v1/me/addresses/{addressId}/default")
    public ResponseEntity<String> setDefaultAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(buyerService.setDefaultAddress(addressId));
    }

    @Operation(summary = "List visible product categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @GetMapping("/api/v1/categories")
    public List<CategoryDTO> getCategories() {
        return buyerService.getCategories();
    }

    @Operation(summary = "Search the storefront catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned."),
            @ApiResponse(responseCode = "400", description = "Invalid or blank search term.")
    })
    @GetMapping("/api/v1/search")
    public List<com.example.onlineshop.product.dto.ProductDTO> searchProducts(@RequestParam String q) {
        return buyerService.searchProducts(q);
    }

    @Operation(summary = "Get homepage content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Homepage content returned."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/api/v1/homepage")
    public java.util.Map<String, Object> getHomepage() {
        return buyerService.getHomepage();
    }

    @Operation(summary = "List active brands for storefront filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active brands."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @GetMapping("/api/v1/brands")
    public List<BrandDTO> getBrands() {
        return buyerService.getBrands();
    }

    @Operation(summary = "Get admin category list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin categories retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:read')")
    public List<CategoryDTO> getAdminCategories() {
        return adminService.getAllCategoriesForAdmin();
    }

    @Operation(summary = "Get admin brand list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin brands retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:read')")
    public List<BrandDTO> getAdminBrands() {
        return adminService.getAllBrandsForAdmin();
    }

    @Operation(summary = "List admin homepage banners")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin banners retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:read')")
    public List<BannerDTO> getAdminBanners() {
        return adminService.getAllBannersForAdmin();
    }

    @Operation(summary = "Create an admin homepage banner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Banner created."),
            @ApiResponse(responseCode = "400", description = "Invalid banner payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:create')")
    public BannerDTO createAdminBanner(@RequestBody BannerDTO bannerDTO) {
        return adminService.createBannerForAdmin(bannerDTO);
    }

    @Operation(summary = "Patch admin banner publication and ordering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Banner updated."),
            @ApiResponse(responseCode = "400", description = "Invalid banner update."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Banner not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:update')")
    public BannerDTO patchAdminBanner(@PathVariable Long bannerId, @RequestBody BannerUpdateDTO patch) {
        return adminService.updateBannerForAdmin(bannerId, patch);
    }

    @Operation(summary = "List admin coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:read')")
    public List<CouponDTO> getAdminCoupons(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        return adminService.getCouponsForAdmin(status, search, sort);
    }

    @Operation(summary = "Create an admin coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon created."),
            @ApiResponse(responseCode = "400", description = "Invalid coupon payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:create')")
    public CouponDTO createAdminCoupon(@RequestBody CouponDTO couponDTO) {
        return adminService.createCouponForAdmin(couponDTO);
    }

    @Operation(summary = "Patch admin coupon details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon updated."),
            @ApiResponse(responseCode = "400", description = "Invalid coupon update."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Coupon not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:update')")
    public CouponDTO patchAdminCoupon(@PathVariable Long couponId, @RequestBody CouponDTO patch) {
        return adminService.updateCouponForAdmin(couponId, patch);
    }

    @Operation(summary = "Update admin coupon status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon status updated."),
            @ApiResponse(responseCode = "400", description = "Unsupported status value."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Coupon not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('coupon:update')")
    public CouponDTO updateAdminCouponStatus(@PathVariable Long couponId, @RequestBody Map<String, String> payload) {
        String status = payload != null ? payload.get("status") : null;
        return adminService.updateCouponStatusForAdmin(couponId, status);
    }

    @Operation(summary = "List admin tax rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tax rules retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:read')")
    public List<TaxRuleDTO> getAdminTaxRules(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime effectiveAt) {
        return adminService.getTaxRulesForAdmin(region, effectiveAt);
    }

    @Operation(summary = "Create an admin tax rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tax rule created."),
            @ApiResponse(responseCode = "400", description = "Invalid or conflicting tax rule."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:create')")
    @ResponseStatus(HttpStatus.CREATED)
    public TaxRuleDTO createAdminTaxRule(@RequestBody @Valid TaxRuleDTO request) {
        return adminService.createTaxRuleForAdmin(request);
    }

    @Operation(summary = "Patch an admin tax rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tax rule updated."),
            @ApiResponse(responseCode = "400", description = "Invalid or conflicting tax rule update."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Tax rule not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('tax:update')")
    public TaxRuleDTO patchAdminTaxRule(@PathVariable Long ruleId, @RequestBody TaxRuleDTO patch) {
        return adminService.updateTaxRuleForAdmin(ruleId, patch);
    }

    @Operation(summary = "List admin shipping zones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping zones retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:read')")
    public List<ShippingZoneDTO> getAdminShippingZones() {
        return adminService.getShippingZonesForAdmin();
    }

    @Operation(summary = "Create an admin shipping zone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipping zone created."),
            @ApiResponse(responseCode = "400", description = "Invalid or ambiguous shipping zone."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:create')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShippingZoneDTO createAdminShippingZone(@RequestBody ShippingZoneDTO request) {
        return adminService.createShippingZoneForAdmin(request);
    }

    @Operation(summary = "Patch an admin shipping zone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping zone updated."),
            @ApiResponse(responseCode = "400", description = "Invalid or ambiguous shipping zone."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Shipping zone not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:update')")
    public ShippingZoneDTO patchAdminShippingZone(@PathVariable Long zoneId, @RequestBody ShippingZoneDTO patch) {
        return adminService.updateShippingZoneForAdmin(zoneId, patch);
    }

    @Operation(summary = "Create an admin shipping method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipping method created."),
            @ApiResponse(responseCode = "400", description = "Invalid or duplicate shipping method."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:create')")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminShippingMethodDTO createAdminShippingMethod(@RequestBody AdminShippingMethodDTO request) {
        return adminService.createShippingMethodForAdmin(request);
    }

    @Operation(summary = "Patch an admin shipping method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping method updated."),
            @ApiResponse(responseCode = "400", description = "Invalid shipping method update."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Shipping method not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('shipping:update')")
    public AdminShippingMethodDTO patchAdminShippingMethod(
            @PathVariable Long methodId, @RequestBody AdminShippingMethodDTO patch) {
        return adminService.updateShippingMethodForAdmin(methodId, patch);
    }

    @Operation(summary = "Archive an admin banner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Banner archived."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Banner not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('banner:delete')")
    public ResponseEntity<Void> archiveAdminBanner(@PathVariable Long bannerId) {
        adminService.archiveBannerForAdmin(bannerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List admin featured collections")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin collections retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:read')")
    public List<FeaturedCollectionDTO> getAdminCollections() {
        return adminService.getAllCollectionsForAdmin();
    }

    @Operation(summary = "Create an admin featured collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collection created."),
            @ApiResponse(responseCode = "400", description = "Invalid collection payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:create')")
    public FeaturedCollectionDTO createAdminCollection(@RequestBody FeaturedCollectionDTO collectionDTO) {
        return adminService.createCollectionForAdmin(collectionDTO);
    }

    @Operation(summary = "Patch featured collection membership and visibility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collection updated."),
            @ApiResponse(responseCode = "400", description = "Invalid collection update."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Collection not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('collection:update')")
    public FeaturedCollectionDTO patchAdminCollection(
            @PathVariable Long collectionId,
            @RequestBody FeaturedCollectionUpdateDTO patch) {
        return adminService.updateCollectionForAdmin(collectionId, patch);
    }

    @Operation(summary = "List admin inventory levels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    public List<AdminInventoryDTO> getAdminInventory() {
        return adminService.getAdminInventory();
    }

    @Operation(summary = "Get paginated inventory movement ledger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory movements retrieved."),
            @ApiResponse(responseCode = "400", description = "Invalid page parameters."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Variant not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    public Page<InventoryMovementDTO> getAdminInventoryMovements(
            @PathVariable Long variantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return adminService.getInventoryMovements(variantId, page, size);
    }

    @Operation(summary = "Adjust variant inventory with an audit record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory adjusted."),
            @ApiResponse(responseCode = "400", description = "Invalid adjustment or insufficient available inventory."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Variant not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:adjust')")
    public InventoryDTO adjustAdminInventory(
            @PathVariable Long variantId,
            @Valid @RequestBody InventoryAdjustmentDTO adjustment) {
        return adminService.adjustInventory(
                variantId,
                adjustment.getQuantityDelta(),
                adjustment.getReason(),
                adjustment.getReference());
    }

    @Operation(summary = "Set low-stock threshold for a variant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Threshold updated."),
            @ApiResponse(responseCode = "400", description = "Threshold must be nonnegative."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Variant not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:update')")
    public InventoryDTO setAdminInventoryThreshold(
            @PathVariable Long variantId,
            @Valid @RequestBody InventoryThresholdDTO threshold) {
        return adminService.setInventoryThreshold(variantId, threshold.getLowStockThreshold());
    }

    @Operation(summary = "List paginated low-stock inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Low-stock inventory retrieved."),
            @ApiResponse(responseCode = "400", description = "Invalid page parameters."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('inventory:read')")
    public Page<AdminInventoryDTO> getAdminLowStockInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return adminService.getLowStockInventory(page, size);
    }

    @Operation(summary = "Create a brand in admin catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand created."),
            @ApiResponse(responseCode = "400", description = "Invalid brand payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:create')")
    public BrandDTO createAdminBrand(@RequestBody BrandDTO brandDTO) {
        return adminService.createBrandForAdmin(brandDTO);
    }

    @Operation(summary = "Get category attribute definitions for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attribute definitions retrieved."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:read')")
    public List<CategoryAttributeDTO> getAdminAttributes() {
        return adminService.getAllCategoryAttributesForAdmin();
    }

    @Operation(summary = "Create a category attribute definition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attribute definition created."),
            @ApiResponse(responseCode = "400", description = "Invalid attribute definition."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:create')")
    public CategoryAttributeDTO createAdminAttribute(@RequestBody CategoryAttributeDTO attributeDTO) {
        return adminService.createCategoryAttributeForAdmin(attributeDTO);
    }

    @Operation(summary = "Patch a category attribute definition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attribute definition updated."),
            @ApiResponse(responseCode = "400", description = "Invalid attribute definition."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Attribute definition not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('attribute:update')")
    public CategoryAttributeDTO patchAdminAttribute(
            @PathVariable Long attributeId,
            @RequestBody CategoryAttributeDTO attributeDTO) {
        return adminService.updateCategoryAttributeForAdmin(attributeId, attributeDTO);
    }

    @Operation(summary = "Create a product variant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Variant created."),
            @ApiResponse(responseCode = "400", description = "Invalid variant payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Product not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:create')")
    public SKUDTO createAdminVariant(
            @PathVariable Long productId,
            @Valid @RequestBody VariantCreateDTO variantDTO) {
        return adminService.createVariantForAdmin(productId, variantDTO);
    }

    @Operation(summary = "Patch a product variant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Variant updated."),
            @ApiResponse(responseCode = "400", description = "Invalid variant payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Product or variant not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
    public SKUDTO patchAdminVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestBody VariantUpdateDTO variantDTO) {
        return adminService.updateVariantForAdmin(productId, variantId, variantDTO);
    }

        @Operation(summary = "Update product variant status")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Variant status updated."),
            @ApiResponse(responseCode = "400", description = "Unsupported status value."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Product or variant not found.")
        })
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('product:update')")
        
        public SKUDTO patchAdminVariantStatus(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestBody Map<String, String> payload) {
        return adminService.updateVariantStatusForAdmin(
            productId, variantId, payload == null ? null : payload.get("status"));
        }

    @Operation(summary = "Patch a brand in admin catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated."),
            @ApiResponse(responseCode = "400", description = "Invalid brand payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Brand not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('brand:update')")
    public BrandDTO patchAdminBrand(@PathVariable Long brandId, @RequestBody BrandDTO brandDTO) {
        return adminService.updateBrandForAdmin(brandId, brandDTO);
    }

    @Operation(summary = "Create a category in admin catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created."),
            @ApiResponse(responseCode = "400", description = "Invalid category payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:create')")
    public CategoryDTO createAdminCategory(@RequestBody CategoryDTO categoryDTO) {
        return adminService.createCategoryForAdmin(categoryDTO);
    }

    @Operation(summary = "Patch an admin category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated."),
            @ApiResponse(responseCode = "400", description = "Invalid category payload."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Category not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:update')")
    public CategoryDTO patchAdminCategory(
            @PathVariable String category,
            @PathVariable(required = false) String subcategory,
            @RequestBody CategoryDTO categoryDTO) {
        return adminService.updateCategoryForAdmin(category, subcategory, categoryDTO);
    }

    @Operation(summary = "Delete an admin category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid category reference."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Category not found.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or hasAuthority('category:delete')")
    public String deleteAdminCategory(
            @PathVariable String category,
            @PathVariable(required = false) String subcategory) {
        adminService.deleteCategoryForAdmin(category, subcategory);
        return "Category deleted successfully.";
    }

    @Operation(summary = "Fetch a category by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found."),
            @ApiResponse(responseCode = "404", description = "Category not found."),
            @ApiResponse(responseCode = "400", description = "Invalid category slug.")
    })
    @GetMapping("/api/v1/categories/{slug}")
    public CategoryDTO getCategoryBySlug(@PathVariable String slug) {
        return buyerService.getCategoryBySlug(slug);
    }

    @Operation(summary = "Revoke all active sessions for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All active sessions revoked."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/me/sessions/revoke-all")
    public ResponseEntity<String> revokeAllSessions() {
        return ResponseEntity.ok(userService.revokeAllSessionsForCurrentUser());
    }

    // --- User management (admin) ---

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid user data."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:create')")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        String jwtToken = userService.registerUser(userDTO);
        return ResponseEntity.ok(jwtToken);
    }

    @Operation(summary = "Anonymize a user account after the retention window")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATOR') or hasAuthority('account:anonymize')")
    public ResponseEntity<String> anonymizeUser(@PathVariable String userId) {
        return ResponseEntity.ok(buyerService.anonymizeUser(userId));
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all users."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:read')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user details."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:read')")
    public UserDTO getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:update')")
    public String updateUser(
            @PathVariable String userId,
            @RequestBody UserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return "User updated successfully.";
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:delete')")
    public String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User deleted successfully.";
    }

    @Operation(summary = "Assign role to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned to user successfully."),
            @ApiResponse(responseCode = "404", description = "User or role not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:assign')")
    public String assignRoleToUser(
            @PathVariable String userId,
            @PathVariable Long roleId) {
        roleService.assignRoleToUser(userId, roleId);
        return "Role assigned to user successfully.";
    }

    @Operation(summary = "Remove role from user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role removed from user successfully."),
            @ApiResponse(responseCode = "404", description = "User or role not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:assign')")
    public String removeRoleFromUser(
            @PathVariable String userId,
            @PathVariable Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
        return "Role removed from user successfully.";
    }

    // --- Role management ---

    @Operation(summary = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully."),
            @ApiResponse(responseCode = "400", description = "Role already exists."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:create')")
    public String createRole(@RequestBody RoleDTO roleDTO) {
        roleService.createRole(roleDTO);
        return "Role created successfully.";
    }

    @Operation(summary = "Get all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all roles."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:read')")
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role details."),
            @ApiResponse(responseCode = "404", description = "Role not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:read')")
    public RoleDTO getRoleById(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId);
    }

    @Operation(summary = "Update a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully."),
            @ApiResponse(responseCode = "404", description = "Role not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:update')")
    public String updateRole(
            @Parameter(description = "ID of the role to be updated", required = true)
            @PathVariable Long roleId,
            @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleId, roleDTO);
        return "Role updated successfully.";
    }

    @Operation(summary = "Delete a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Role not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:delete')")
    public String deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return "Role deleted successfully.";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleLocalValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .filter(error -> "password".equals(error.getField()))
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Request validation failed.");
        Map<String, Object> body = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidArgument(IllegalArgumentException exception) {
        Map<String, Object> body = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
