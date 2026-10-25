package com.example.onlineshop.common.service;

import com.example.onlineshop.content.dto.*;
import com.example.onlineshop.order.dto.*;
import com.example.onlineshop.product.dto.*;
import com.example.onlineshop.promotion.dto.*;
import com.example.onlineshop.shipping.dto.*;
import com.example.onlineshop.user.dto.*;
import com.example.onlineshop.content.entity.*;
import com.example.onlineshop.content.repository.*;
import com.example.onlineshop.order.entity.*;
import com.example.onlineshop.order.repository.*;
import com.example.onlineshop.product.entity.*;
import com.example.onlineshop.product.repository.*;
import com.example.onlineshop.promotion.entity.*;
import com.example.onlineshop.promotion.repository.*;
import com.example.onlineshop.shipping.entity.*;
import com.example.onlineshop.shipping.repository.*;
import com.example.onlineshop.user.entity.*;
import com.example.onlineshop.user.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.net.URI;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.product.service.ProductService;

@Service
public class AdminService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private FeaturedCollectionRepository featuredCollectionRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private TaxRuleRepository taxRuleRepository;

    @Autowired
    private ShippingZoneRepository shippingZoneRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    public String createProductForAdmin(ProductDTO productDTO) {
        if (productDTO == null || productDTO.getName() == null || productDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (productDTO.getDescription() == null || productDTO.getDescription().isBlank()) {
            throw new IllegalArgumentException("Product description is required");
        }

        Product product = new Product();
        product.setName(productDTO.getName().trim());
        product.setDescription(productDTO.getDescription().trim());
        product.setSlug(CategoryDTO.slugify(productDTO.getName()));
        product.setApproved(false);
        product.setAvailable(false);

        if (productDTO.getCategoryDTO() != null) {
            Category category = categoryRepository.findByCategoryAndSubcategory(
                    productDTO.getCategoryDTO().getCategory(),
                    productDTO.getCategoryDTO().getSubcategory())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCategory(productDTO.getCategoryDTO().getCategory());
                        newCategory.setSubcategory(productDTO.getCategoryDTO().getSubcategory());
                        return categoryRepository.save(newCategory);
                    });
            product.setCategory(category);
        }

        if (productDTO.getBrandName() != null && !productDTO.getBrandName().isBlank()) {
            Brand brand = brandRepository.findByActiveTrueOrderByNameAsc().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(productDTO.getBrandName().trim()))
                    .findFirst()
                    .orElseGet(() -> {
                        Brand newBrand = new Brand();
                        newBrand.setName(productDTO.getBrandName().trim());
                        newBrand.setActive(true);
                        return brandRepository.save(newBrand);
                    });
            product.setBrand(brand);
        }

        productRepository.save(product);
        return "Product created successfully.";
    }

    // Add a new product along with SKUs
    public void addProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        productRepository.save(product);

        for (SKUDTO skuDTO : productDTO.getSkuDTOs()) {
            SKU sku = new SKU();
            sku.setSize(skuDTO.getSize());
            sku.setColor(skuDTO.getColor());
            sku.setSkuCode(skuDTO.getSkuCode());
            sku.setProduct(product);
            sku = skuRepository.save(sku);
            if (skuDTO.getInventoryDTOS() != null) {
                for (InventoryDTO inventoryDTO : skuDTO.getInventoryDTOS()) {
                    Inventory inventory = new Inventory();
                    inventory.setSku(sku);
                    inventory.setCostPrice(inventoryDTO.getCostPrice());
                    inventory.setSellingPrice(inventoryDTO.getSellingPrice());
                    inventory.setStockAvailable(inventoryDTO.getStockAvailable());
                    inventoryRepository.save(inventory);
                }
            }
        }
    }

    public void addInventory(Long skuId, InventoryDTO inventoryDTO) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        Inventory inventory = new Inventory();
        inventory.setSku(sku);
        inventory.setCostPrice(inventoryDTO.getCostPrice());
        inventory.setSellingPrice(inventoryDTO.getSellingPrice());
        inventory.setStockAvailable(inventoryDTO.getStockAvailable());
        inventoryRepository.save(inventory);
    }

    public void updateStock(Long inventoryId, int newStock) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setStockAvailable(newStock);
        inventoryRepository.save(inventory);
    }

    public void updateProductPricing(Long inventoryId, double newSellingPrice, double newCostPrice) {
        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setSellingPrice(newSellingPrice);
        inventory.setCostPrice(newCostPrice);
        inventoryRepository.save(inventory);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderDTO::new).collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProductsForAdmin() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getAllCategoriesForAdmin() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public List<BrandDTO> getAllBrandsForAdmin() {
        return brandRepository.findAll().stream()
                .map(BrandDTO::new)
                .collect(Collectors.toList());
    }

    public List<BannerDTO> getAllBannersForAdmin() {
        return bannerRepository.findAllByOrderByDisplayOrderAscBannerIdAsc().stream()
                .map(BannerDTO::new)
                .collect(Collectors.toList());
    }

    public BannerDTO createBannerForAdmin(BannerDTO bannerDTO) {
        if (bannerDTO == null || bannerDTO.getTitle() == null || bannerDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Banner title is required");
        }
        if (!isSafeBannerUrl(bannerDTO.getMediaUrl()) ||
                (bannerDTO.getLinkUrl() != null && !bannerDTO.getLinkUrl().isBlank()
                        && !isSafeBannerUrl(bannerDTO.getLinkUrl()))) {
            throw new IllegalArgumentException("Banner media and link must use HTTPS or a root-relative path");
        }
        if (bannerDTO.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
        if (bannerDTO.getStartsAt() != null && bannerDTO.getEndsAt() != null
                && bannerDTO.getStartsAt().isAfter(bannerDTO.getEndsAt())) {
            throw new IllegalArgumentException("Banner start time must not be after end time");
        }

        Banner banner = new Banner();
        banner.setTitle(bannerDTO.getTitle().trim());
        banner.setMediaUrl(bannerDTO.getMediaUrl().trim());
        banner.setLinkUrl(bannerDTO.getLinkUrl() == null || bannerDTO.getLinkUrl().isBlank()
                ? null : bannerDTO.getLinkUrl().trim());
        banner.setDisplayOrder(bannerDTO.getDisplayOrder());
        banner.setActive(bannerDTO.isActive());
        banner.setStartsAt(bannerDTO.getStartsAt());
        banner.setEndsAt(bannerDTO.getEndsAt());
        banner.setArchived(false);
        return new BannerDTO(bannerRepository.save(banner));
    }

    public BannerDTO updateBannerForAdmin(Long bannerId, BannerUpdateDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Banner update is required");
        }
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        if (patch.getTitle() != null) {
            if (patch.getTitle().isBlank()) {
                throw new IllegalArgumentException("Banner title cannot be blank");
            }
            banner.setTitle(patch.getTitle().trim());
        }
        if (patch.getMediaUrl() != null) {
            if (!isSafeBannerUrl(patch.getMediaUrl())) {
                throw new IllegalArgumentException("Banner media URL must use HTTPS or a root-relative path");
            }
            banner.setMediaUrl(patch.getMediaUrl().trim());
        }
        if (patch.getLinkUrl() != null) {
            if (!patch.getLinkUrl().isBlank() && !isSafeBannerUrl(patch.getLinkUrl())) {
                throw new IllegalArgumentException("Banner link must use HTTPS or a root-relative path");
            }
            banner.setLinkUrl(patch.getLinkUrl().isBlank() ? null : patch.getLinkUrl().trim());
        }
        if (patch.getDisplayOrder() != null) {
            if (patch.getDisplayOrder() < 0) {
                throw new IllegalArgumentException("Display order cannot be negative");
            }
            banner.setDisplayOrder(patch.getDisplayOrder());
        }
        if (Boolean.TRUE.equals(patch.getActive()) && banner.isArchived()) {
            throw new IllegalArgumentException("Archived banners cannot be published");
        }
        if (patch.getActive() != null) {
            banner.setActive(patch.getActive());
        }

        if (patch.getStartsAt() != null) {
            banner.setStartsAt(patch.getStartsAt());
        }
        if (patch.getEndsAt() != null) {
            banner.setEndsAt(patch.getEndsAt());
        }
        if (banner.getStartsAt() != null && banner.getEndsAt() != null
                && banner.getStartsAt().isAfter(banner.getEndsAt())) {
            throw new IllegalArgumentException("Banner start time must not be after end time");
        }

        return new BannerDTO(bannerRepository.save(banner));
    }

    public void archiveBannerForAdmin(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setActive(false);
        banner.setArchived(true);
        bannerRepository.save(banner);
    }

    public List<FeaturedCollectionDTO> getAllCollectionsForAdmin() {
        return featuredCollectionRepository.findAllByOrderByDisplayOrderAscCollectionIdAsc().stream()
                .map(FeaturedCollectionDTO::new)
                .collect(Collectors.toList());
    }

    public List<CouponDTO> getCouponsForAdmin(String status, String search, String sort) {
        List<Coupon> coupons = couponRepository.findAll();
        String normalizedStatus = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        List<Coupon> filtered = coupons.stream()
                .filter(coupon -> "ALL".equals(normalizedStatus) || ("ACTIVE".equals(normalizedStatus) && coupon.isActive())
                        || ("INACTIVE".equals(normalizedStatus) && !coupon.isActive()))
                .filter(coupon -> normalizedSearch.isBlank()
                        || coupon.getCode().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || (coupon.getDescription() != null && coupon.getDescription().toLowerCase(Locale.ROOT).contains(normalizedSearch)))
                .collect(Collectors.toList());

        if ("DESC".equalsIgnoreCase(sort)) {
            filtered.sort((left, right) -> Long.compare(right.getCouponId(), left.getCouponId()));
        } else {
            filtered.sort((left, right) -> Long.compare(left.getCouponId(), right.getCouponId()));
        }

        return filtered.stream().map(CouponDTO::new).collect(Collectors.toList());
    }

        public List<TaxRuleDTO> getTaxRulesForAdmin(String region, LocalDateTime effectiveAt) {
        String normalizedRegion = region == null ? null : region.trim();
        return taxRuleRepository.findAllByOrderByRegionAscEffectiveFromDesc().stream()
            .filter(rule -> normalizedRegion == null || normalizedRegion.isBlank()
                || rule.getRegion().equalsIgnoreCase(normalizedRegion))
            .filter(rule -> effectiveAt == null || (!rule.getEffectiveFrom().isAfter(effectiveAt)
                && (rule.getEffectiveTo() == null || !rule.getEffectiveTo().isBefore(effectiveAt))))
            .map(TaxRuleDTO::new)
            .collect(Collectors.toList());
        }

    public List<ShippingZoneDTO> getShippingZonesForAdmin() {
        return shippingZoneRepository.findAllByOrderByPriorityAscZoneIdAsc().stream()
                .map(ShippingZoneDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminShippingMethodDTO createShippingMethodForAdmin(AdminShippingMethodDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Shipping method payload is required");
        }
        String code = request.getCode() == null ? "" : request.getCode().trim().toUpperCase(Locale.ROOT);
        String label = request.getLabel() == null ? "" : request.getLabel().trim();
        String estimate = request.getEstimate() == null ? "" : request.getEstimate().trim();
        if (!code.matches("[A-Z0-9][A-Z0-9_-]{0,31}")) {
            throw new IllegalArgumentException("A valid shipping method code is required");
        }
        if (shippingMethodRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Shipping method code already exists");
        }
        if (label.isBlank() || label.length() > 100) {
            throw new IllegalArgumentException("Shipping method label must contain 1 to 100 characters");
        }
        if (estimate.isBlank() || estimate.length() > 100) {
            throw new IllegalArgumentException("Shipping estimate must contain 1 to 100 characters");
        }
        if (request.getFee() == null || request.getFee().signum() < 0
                || request.getFee().scale() > 2 || request.getFee().precision() > 12) {
            throw new IllegalArgumentException("Shipping fee must be nonnegative with at most two decimal places");
        }
        BigDecimal threshold = request.getFreeShippingThreshold();
        if (threshold != null && (threshold.signum() < 0 || threshold.scale() > 2 || threshold.precision() > 12)) {
            throw new IllegalArgumentException("Free-shipping threshold must be nonnegative with at most two decimal places");
        }

        ShippingMethod method = new ShippingMethod();
        method.setCode(code);
        method.setLabel(label);
        method.setFee(request.getFee());
        method.setFreeShippingThreshold(threshold);
        method.setEstimate(estimate);
        method.setActive(request.getActive() == null || request.getActive());
        return new AdminShippingMethodDTO(shippingMethodRepository.save(method));
    }

    @Transactional
    public AdminShippingMethodDTO updateShippingMethodForAdmin(Long methodId, AdminShippingMethodDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Shipping method update is required");
        }
        ShippingMethod method = shippingMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        if (patch.getCode() != null) {
            String code = patch.getCode().trim().toUpperCase(Locale.ROOT);
            if (!code.matches("[A-Z0-9][A-Z0-9_-]{0,31}")) {
                throw new IllegalArgumentException("A valid shipping method code is required");
            }
            if (!code.equalsIgnoreCase(method.getCode())
                    && shippingMethodRepository.existsByCodeIgnoreCaseAndMethodIdNot(code, methodId)) {
                throw new IllegalArgumentException("Shipping method code already exists");
            }
            method.setCode(code);
        }
        if (patch.getLabel() != null) {
            String label = patch.getLabel().trim();
            if (label.isBlank() || label.length() > 100) {
                throw new IllegalArgumentException("Shipping method label must contain 1 to 100 characters");
            }
            method.setLabel(label);
        }
        if (patch.getEstimate() != null) {
            String estimate = patch.getEstimate().trim();
            if (estimate.isBlank() || estimate.length() > 100) {
                throw new IllegalArgumentException("Shipping estimate must contain 1 to 100 characters");
            }
            method.setEstimate(estimate);
        }
        if (patch.getFee() != null) {
            validateShippingMoney(patch.getFee(), "Shipping fee");
            method.setFee(patch.getFee());
        }
        if (patch.getFreeShippingThreshold() != null) {
            validateShippingMoney(patch.getFreeShippingThreshold(), "Free-shipping threshold");
            method.setFreeShippingThreshold(patch.getFreeShippingThreshold());
        }
        if (patch.getActive() != null) {
            method.setActive(patch.getActive());
        }
        return new AdminShippingMethodDTO(shippingMethodRepository.save(method));
    }

    private void validateShippingMoney(BigDecimal amount, String label) {
        if (amount.signum() < 0 || amount.scale() > 2 || amount.precision() > 12) {
            throw new IllegalArgumentException(label + " must be nonnegative with at most two decimal places");
        }
    }

    @Transactional
    public ShippingZoneDTO createShippingZoneForAdmin(ShippingZoneDTO request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Shipping zone name is required");
        }
        if (request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Shipping zone name cannot exceed 100 characters");
        }
        if (request.getPriority() < 0) {
            throw new IllegalArgumentException("Shipping zone priority cannot be negative");
        }

        List<String> regions = normalizeZoneMatchers(request.getRegionCodes(), "region code");
        List<String> postalPrefixes = normalizeZoneMatchers(request.getPostalCodePrefixes(), "postal prefix");
        boolean active = request.isActive();
        if (active) {
            boolean ambiguous = shippingZoneRepository.findAll().stream()
                    .filter(ShippingZone::isActive)
                    .filter(existing -> existing.getPriority() == request.getPriority())
                    .anyMatch(existing -> zoneMatchersOverlap(regions, postalPrefixes,
                            existing.getRegionCodes(), existing.getPostalCodePrefixes()));
            if (ambiguous) {
                throw new IllegalArgumentException("Active shipping zones at the same priority cannot have overlapping match rules");
            }
        }

        ShippingZone zone = new ShippingZone();
        zone.setName(request.getName().trim());
        zone.setRegionCodes(regions);
        zone.setPostalCodePrefixes(postalPrefixes);
        zone.setPriority(request.getPriority());
        zone.setActive(active);
        return new ShippingZoneDTO(shippingZoneRepository.save(zone));
    }

    @Transactional
    public ShippingZoneDTO updateShippingZoneForAdmin(Long zoneId, ShippingZoneDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Shipping zone update is required");
        }
        ShippingZone zone = shippingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Shipping zone not found"));

        String name = zone.getName();
        if (patch.getName() != null) {
            name = patch.getName().trim();
            if (name.isBlank() || name.length() > 100) {
                throw new IllegalArgumentException("Shipping zone name must contain 1 to 100 characters");
            }
        }
        List<String> regions = patch.getRegionCodes() == null ? zone.getRegionCodes()
                : normalizeZoneMatchers(patch.getRegionCodes(), "region code");
        List<String> postalPrefixes = patch.getPostalCodePrefixes() == null ? zone.getPostalCodePrefixes()
                : normalizeZoneMatchers(patch.getPostalCodePrefixes(), "postal prefix");
        int priority = patch.getPriorityOrNull() == null ? zone.getPriority() : patch.getPriorityOrNull();
        if (priority < 0) {
            throw new IllegalArgumentException("Shipping zone priority cannot be negative");
        }
        boolean active = patch.getActive() == null ? zone.isActive() : patch.getActive();

        if (active) {
            boolean ambiguous = shippingZoneRepository.findAll().stream()
                    .filter(ShippingZone::isActive)
                    .filter(existing -> !existing.getZoneId().equals(zoneId))
                    .filter(existing -> existing.getPriority() == priority)
                    .anyMatch(existing -> zoneMatchersOverlap(regions, postalPrefixes,
                            existing.getRegionCodes(), existing.getPostalCodePrefixes()));
            if (ambiguous) {
                throw new IllegalArgumentException("Active shipping zones at the same priority cannot have overlapping match rules");
            }
        }

        zone.setName(name);
        zone.setRegionCodes(regions);
        zone.setPostalCodePrefixes(postalPrefixes);
        zone.setPriority(priority);
        zone.setActive(active);
        return new ShippingZoneDTO(shippingZoneRepository.save(zone));
    }

    private List<String> normalizeZoneMatchers(List<String> values, String label) {
        if (values == null) {
            return List.of();
        }
        List<String> normalized = values.stream()
                .map(value -> value == null ? "" : value.trim().toUpperCase(Locale.ROOT))
                .peek(value -> {
                    if (!value.matches("[A-Z0-9][A-Z0-9_-]{0,15}")) {
                        throw new IllegalArgumentException("Each " + label + " must be a valid non-empty code");
                    }
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (normalized.size() != values.size()) {
            throw new IllegalArgumentException("Duplicate " + label + " values are not allowed");
        }
        return normalized;
    }

    private boolean zoneMatchersOverlap(List<String> firstRegions, List<String> firstPrefixes,
            List<String> secondRegions, List<String> secondPrefixes) {
        boolean regionOverlap = firstRegions.isEmpty() || secondRegions.isEmpty()
                || firstRegions.stream().anyMatch(secondRegions::contains);
        boolean postalOverlap = firstPrefixes.isEmpty() || secondPrefixes.isEmpty()
                || firstPrefixes.stream().anyMatch(first -> secondPrefixes.stream()
                        .anyMatch(second -> first.startsWith(second) || second.startsWith(first)));
        return regionOverlap && postalOverlap;
    }

    @Transactional
    public TaxRuleDTO createTaxRuleForAdmin(TaxRuleDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Tax rule payload is required");
        }

        String region = request.getRegion() == null ? "" : request.getRegion().trim().toUpperCase(Locale.ROOT);
        if (!region.matches("[A-Z0-9][A-Z0-9_-]{0,15}")) {
            throw new IllegalArgumentException("A valid tax region is required");
        }
        if (request.getRate() == null || request.getRate().signum() < 0
                || request.getRate().compareTo(BigDecimal.ONE) > 0
                || request.getRate().stripTrailingZeros().scale() > 4) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1 with at most four decimal places");
        }
        if (request.getEffectiveFrom() == null) {
            throw new IllegalArgumentException("Tax rule effective start is required");
        }
        if (request.getEffectiveTo() != null && !request.getEffectiveTo().isAfter(request.getEffectiveFrom())) {
            throw new IllegalArgumentException("Tax rule effective end must be after its start");
        }

        boolean active = request.isActive();
        if (active) {
            boolean overlaps = taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue(region).stream()
                    .anyMatch(existing -> intervalsOverlap(
                            request.getEffectiveFrom(), request.getEffectiveTo(),
                            existing.getEffectiveFrom(), existing.getEffectiveTo()));
            if (overlaps) {
                throw new IllegalArgumentException("An active tax rule already covers part of this region and date range");
            }
        }

        TaxRule rule = new TaxRule();
        rule.setRegion(region);
        rule.setRate(request.getRate());
        rule.setTaxInclusive(request.isTaxInclusive());
        rule.setEffectiveFrom(request.getEffectiveFrom());
        rule.setEffectiveTo(request.getEffectiveTo());
        rule.setActive(active);
        return new TaxRuleDTO(taxRuleRepository.save(rule));
    }

    @Transactional
    public TaxRuleDTO updateTaxRuleForAdmin(Long ruleId, TaxRuleDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Tax rule update is required");
        }
        TaxRule rule = taxRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Tax rule not found"));

        String region = rule.getRegion();
        if (patch.getRegion() != null) {
            region = patch.getRegion().trim().toUpperCase(Locale.ROOT);
            if (!region.matches("[A-Z0-9][A-Z0-9_-]{0,15}")) {
                throw new IllegalArgumentException("A valid tax region is required");
            }
        }

        BigDecimal rate = patch.getRate() == null ? rule.getRate() : patch.getRate();
        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0
                || rate.stripTrailingZeros().scale() > 4) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1 with at most four decimal places");
        }
        LocalDateTime effectiveFrom = patch.getEffectiveFrom() == null
                ? rule.getEffectiveFrom() : patch.getEffectiveFrom();
        LocalDateTime effectiveTo = patch.getEffectiveTo() == null
                ? rule.getEffectiveTo() : patch.getEffectiveTo();
        if (effectiveTo != null && !effectiveTo.isAfter(effectiveFrom)) {
            throw new IllegalArgumentException("Tax rule effective end must be after its start");
        }
        boolean active = patch.getActive() == null ? rule.isActive() : patch.getActive();
        if (active) {
            boolean overlaps = taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue(region).stream()
                    .filter(existing -> !existing.getRuleId().equals(ruleId))
                    .anyMatch(existing -> intervalsOverlap(
                            effectiveFrom, effectiveTo,
                            existing.getEffectiveFrom(), existing.getEffectiveTo()));
            if (overlaps) {
                throw new IllegalArgumentException("An active tax rule already covers part of this region and date range");
            }
        }

        rule.setRegion(region);
        rule.setRate(rate);
        if (patch.getTaxInclusive() != null) {
            rule.setTaxInclusive(patch.getTaxInclusive());
        }
        rule.setEffectiveFrom(effectiveFrom);
        rule.setEffectiveTo(effectiveTo);
        rule.setActive(active);
        return new TaxRuleDTO(taxRuleRepository.save(rule));
    }

    private boolean intervalsOverlap(LocalDateTime firstStart, LocalDateTime firstEnd,
            LocalDateTime secondStart, LocalDateTime secondEnd) {
        return (firstEnd == null || secondStart.isBefore(firstEnd))
                && (secondEnd == null || firstStart.isBefore(secondEnd));
    }

    @Transactional
    public CouponDTO createCouponForAdmin(CouponDTO couponDTO) {
        if (couponDTO == null) {
            throw new IllegalArgumentException("Coupon payload is required");
        }
        String code = couponDTO.getCode() == null ? null : couponDTO.getCode().trim();
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Coupon code is required");
        }
        String type = couponDTO.getType() == null ? "PERCENTAGE" : couponDTO.getType().trim().toUpperCase(Locale.ROOT);
        if (!List.of("PERCENTAGE", "FIXED").contains(type)) {
            throw new IllegalArgumentException("Coupon type must be PERCENTAGE or FIXED");
        }
        if (couponDTO.getValue() <= 0) {
            throw new IllegalArgumentException("Coupon value must be greater than zero");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(code.toUpperCase(Locale.ROOT));
        coupon.setDescription(couponDTO.getDescription() == null || couponDTO.getDescription().isBlank() ? null : couponDTO.getDescription().trim());
        coupon.setType(type);
        coupon.setValue(couponDTO.getValue());
        coupon.setMinSpend(couponDTO.getMinSpend() < 0 ? 0 : couponDTO.getMinSpend());
        coupon.setMaxDiscount(couponDTO.getMaxDiscount() < 0 ? 0 : couponDTO.getMaxDiscount());
        coupon.setUsageLimit(couponDTO.getUsageLimit() < 0 ? 0 : couponDTO.getUsageLimit());
        coupon.setUsedCount(0);
        coupon.setActive(Boolean.TRUE.equals(couponDTO.getActive()));
        coupon.setStackable(Boolean.TRUE.equals(couponDTO.getStackable()));
        coupon.setStartAt(couponDTO.getStartAt());
        coupon.setEndAt(couponDTO.getEndAt());
        if (coupon.getStartAt() != null && coupon.getEndAt() != null && coupon.getStartAt().isAfter(coupon.getEndAt())) {
            throw new IllegalArgumentException("Coupon start time must not be after end time");
        }
        return new CouponDTO(couponRepository.save(coupon));
    }

    @Transactional
    public CouponDTO updateCouponForAdmin(Long couponId, CouponDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Coupon update is required");
        }
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (patch.getCode() != null && !patch.getCode().isBlank()) {
            coupon.setCode(patch.getCode().trim().toUpperCase(Locale.ROOT));
        }
        if (patch.getDescription() != null) {
            coupon.setDescription(patch.getDescription().isBlank() ? null : patch.getDescription().trim());
        }
        if (patch.getType() != null && !patch.getType().isBlank()) {
            String type = patch.getType().trim().toUpperCase(Locale.ROOT);
            if (!List.of("PERCENTAGE", "FIXED").contains(type)) {
                throw new IllegalArgumentException("Coupon type must be PERCENTAGE or FIXED");
            }
            coupon.setType(type);
        }
        if (patch.getValue() > 0) {
            coupon.setValue(patch.getValue());
        }
        if (patch.getMinSpend() >= 0) {
            coupon.setMinSpend(patch.getMinSpend());
        }
        if (patch.getMaxDiscount() >= 0) {
            coupon.setMaxDiscount(patch.getMaxDiscount());
        }
        if (patch.getUsageLimit() >= 0) {
            coupon.setUsageLimit(patch.getUsageLimit());
        }
        if (patch.getStartAt() != null) {
            coupon.setStartAt(patch.getStartAt());
        }
        if (patch.getEndAt() != null) {
            coupon.setEndAt(patch.getEndAt());
        }
        if (coupon.getStartAt() != null && coupon.getEndAt() != null && coupon.getStartAt().isAfter(coupon.getEndAt())) {
            throw new IllegalArgumentException("Coupon start time must not be after end time");
        }
        if (patch.getActive() != null && !patch.getActive().equals(coupon.isActive())) {
            coupon.setActive(patch.getActive());
        }
        if (patch.getStackable() != null && !patch.getStackable().equals(coupon.isStackable())) {
            coupon.setStackable(patch.getStackable());
        }
        return new CouponDTO(couponRepository.save(coupon));
    }

    @Transactional
    public CouponDTO updateCouponStatusForAdmin(Long couponId, String status) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        String resolved = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!List.of("ACTIVE", "INACTIVE", "DRAFT").contains(resolved)) {
            throw new IllegalArgumentException("Unsupported coupon status");
        }
        coupon.setActive("ACTIVE".equals(resolved) || ("DRAFT".equals(resolved) && coupon.isActive()));
        return new CouponDTO(couponRepository.save(coupon));
    }

    public List<AdminInventoryDTO> getAdminInventory() {
        return inventoryRepository.findAll().stream()
                .map(AdminInventoryDTO::new)
                .collect(Collectors.toList());
    }

    public Page<InventoryMovementDTO> getInventoryMovements(Long variantId, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be nonnegative and size must be between 1 and 100");
        }
        skuRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Variant not found"));
        return inventoryMovementRepository
                .findByInventory_Sku_SkuIdOrderByOccurredAtDescMovementIdDesc(
                        variantId, PageRequest.of(page, size, Sort.unsorted()))
                .map(InventoryMovementDTO::new);
    }

    @Transactional
    public InventoryDTO adjustInventory(Long variantId, int quantityDelta, String reason, String reference) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Adjustment reason is required");
        }
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        List<Inventory> inventories = inventoryRepository.findBySku(sku);
        if (inventories.size() != 1) {
            throw new IllegalStateException("Variant must have exactly one inventory record");
        }

        Inventory inventory = inventories.get(0);
        int adjustedAvailable;
        try {
            adjustedAvailable = Math.addExact(inventory.getStockAvailable(), quantityDelta);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Adjustment exceeds supported inventory range", exception);
        }
        if (adjustedAvailable < 0) {
            throw new IllegalArgumentException("Adjustment cannot make available inventory negative");
        }

        inventory.setStockAvailable(adjustedAvailable);
        Inventory savedInventory = inventoryRepository.save(inventory);

        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(savedInventory);
        movement.setQuantityDelta(quantityDelta);
        movement.setReason(reason.trim());
        movement.setActorId(authenticationHelper.getCurrentUserId());
        movement.setReference(reference == null || reference.isBlank() ? null : reference.trim());
        movement.setOccurredAt(LocalDateTime.now());
        inventoryMovementRepository.save(movement);
        return new InventoryDTO(savedInventory);
    }

    @Transactional
    public InventoryDTO setInventoryThreshold(Long variantId, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low-stock threshold cannot be negative");
        }
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        List<Inventory> inventories = inventoryRepository.findBySku(sku);
        if (inventories.size() != 1) {
            throw new IllegalStateException("Variant must have exactly one inventory record");
        }
        Inventory inventory = inventories.get(0);
        inventory.setLowStockThreshold(threshold);
        return new InventoryDTO(inventoryRepository.save(inventory));
    }

    public Page<AdminInventoryDTO> getLowStockInventory(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be nonnegative and size must be between 1 and 100");
        }
        return inventoryRepository.findLowStock(PageRequest.of(page, size, Sort.by("inventoryId").ascending()))
                .map(AdminInventoryDTO::new);
    }

    @Transactional
    public FeaturedCollectionDTO createCollectionForAdmin(FeaturedCollectionDTO collectionDTO) {
        if (collectionDTO == null || collectionDTO.getName() == null || collectionDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Collection name is required");
        }
        if (collectionDTO.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }

        String slug = CategoryDTO.slugify(collectionDTO.getName());
        if (slug.isBlank() || featuredCollectionRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Collection name must produce a unique slug");
        }

        List<Long> requestedProductIds = collectionDTO.getProductIds() == null
                ? List.of() : collectionDTO.getProductIds();
        if (requestedProductIds.stream().anyMatch(java.util.Objects::isNull)
                || requestedProductIds.stream().distinct().count() != requestedProductIds.size()) {
            throw new IllegalArgumentException("Product membership IDs must be unique and non-null");
        }

        List<Product> foundProducts = productRepository.findAllById(requestedProductIds);
        java.util.Map<Long, Product> productsById = foundProducts.stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));
        if (productsById.size() != requestedProductIds.size()) {
            throw new IllegalArgumentException("One or more collection products were not found");
        }

        FeaturedCollection collection = new FeaturedCollection();
        collection.setName(collectionDTO.getName().trim());
        collection.setSlug(slug);
        collection.setActive(collectionDTO.isActive());
        collection.setDisplayOrder(collectionDTO.getDisplayOrder());
        collection.setProducts(requestedProductIds.stream().map(productsById::get).collect(Collectors.toList()));
        return new FeaturedCollectionDTO(featuredCollectionRepository.save(collection));
    }

    @Transactional
    public FeaturedCollectionDTO updateCollectionForAdmin(Long collectionId, FeaturedCollectionUpdateDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Collection update is required");
        }
        FeaturedCollection collection = featuredCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if (patch.getActive() != null) {
            collection.setActive(patch.getActive());
        }
        if (patch.getDisplayOrder() != null) {
            if (patch.getDisplayOrder() < 0) {
                throw new IllegalArgumentException("Display order cannot be negative");
            }
            collection.setDisplayOrder(patch.getDisplayOrder());
        }
        if (patch.getProductIds() != null) {
            List<Long> productIds = patch.getProductIds();
            if (productIds.stream().anyMatch(java.util.Objects::isNull)
                    || productIds.stream().distinct().count() != productIds.size()) {
                throw new IllegalArgumentException("Product membership IDs must be unique and non-null");
            }
            List<Product> foundProducts = productRepository.findAllById(productIds);
            java.util.Map<Long, Product> productsById = foundProducts.stream()
                    .collect(Collectors.toMap(Product::getProductId, product -> product));
            if (productsById.size() != productIds.size()) {
                throw new IllegalArgumentException("One or more collection products were not found");
            }
            collection.setProducts(productIds.stream().map(productsById::get).collect(Collectors.toList()));
        }

        return new FeaturedCollectionDTO(featuredCollectionRepository.save(collection));
    }

    private boolean isSafeBannerUrl(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("/")) {
            return !trimmed.startsWith("//") && !trimmed.contains("\\");
        }
        try {
            URI uri = URI.create(trimmed);
            return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public List<CategoryAttributeDTO> getAllCategoryAttributesForAdmin() {
        return categoryAttributeRepository.findAllByOrderByCategoryAscSubcategoryAscNameAsc().stream()
                .map(CategoryAttributeDTO::new)
                .collect(Collectors.toList());
    }

    public CategoryAttributeDTO createCategoryAttributeForAdmin(CategoryAttributeDTO attributeDTO) {
        if (attributeDTO == null || isBlank(attributeDTO.getCategory()) || isBlank(attributeDTO.getSubcategory())
                || isBlank(attributeDTO.getName()) || isBlank(attributeDTO.getDataType())) {
            throw new IllegalArgumentException("Category, subcategory, name, and data type are required");
        }

        String category = attributeDTO.getCategory().trim();
        String subcategory = attributeDTO.getSubcategory().trim();
        String name = attributeDTO.getName().trim();
        String dataType = attributeDTO.getDataType().trim().toUpperCase(Locale.ROOT);
        if (!List.of("STRING", "NUMBER", "BOOLEAN", "ENUM").contains(dataType)) {
            throw new IllegalArgumentException("Unsupported attribute data type");
        }

        List<String> allowedValues = attributeDTO.getAllowedValues() == null ? List.of()
                : attributeDTO.getAllowedValues().stream()
                        .filter(value -> value != null && !value.isBlank())
                        .map(String::trim)
                        .distinct()
                        .toList();
        if ("ENUM".equals(dataType) && allowedValues.isEmpty()) {
            throw new IllegalArgumentException("ENUM attributes require allowed values");
        }
        if (!"ENUM".equals(dataType) && !allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Allowed values are only supported for ENUM attributes");
        }
        if (categoryAttributeRepository.existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCase(
                category, subcategory, name)) {
            throw new IllegalArgumentException("Attribute already exists for this category");
        }

        CategoryAttribute attribute = new CategoryAttribute();
        attribute.setCategory(category);
        attribute.setSubcategory(subcategory);
        attribute.setName(name);
        attribute.setDataType(dataType);
        attribute.setAllowedValues(allowedValues);
        attribute.setFilterable(attributeDTO.isFilterable());
        return new CategoryAttributeDTO(categoryAttributeRepository.save(attribute));
    }

    public CategoryAttributeDTO updateCategoryAttributeForAdmin(Long attributeId, CategoryAttributeDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Attribute update is required");
        }

        CategoryAttribute attribute = categoryAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Category attribute not found"));

        String category = patch.getCategory() == null ? attribute.getCategory() : patch.getCategory().trim();
        String subcategory = patch.getSubcategory() == null ? attribute.getSubcategory() : patch.getSubcategory().trim();
        String name = patch.getName() == null ? attribute.getName() : patch.getName().trim();
        if (category.isBlank() || subcategory.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("Category, subcategory, and name cannot be blank");
        }

        String dataType = patch.getDataType() == null ? attribute.getDataType()
                : patch.getDataType().trim().toUpperCase(Locale.ROOT);
        if (!List.of("STRING", "NUMBER", "BOOLEAN", "ENUM").contains(dataType)) {
            throw new IllegalArgumentException("Unsupported attribute data type");
        }
        List<String> allowedValues = patch.getAllowedValues() == null
                ? new ArrayList<>(attribute.getAllowedValues())
                : patch.getAllowedValues().stream()
                        .filter(value -> value != null && !value.isBlank())
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));
        if (!"ENUM".equals(dataType)) {
            allowedValues.clear();
        } else if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("ENUM attributes require allowed values");
        }
        if (categoryAttributeRepository.existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCaseAndIdNot(
                category, subcategory, name, attributeId)) {
            throw new IllegalArgumentException("Attribute already exists for this category");
        }

        attribute.setCategory(category);
        attribute.setSubcategory(subcategory);
        attribute.setName(name);
        attribute.setDataType(dataType);
        attribute.setAllowedValues(allowedValues);
        if (patch.getFilterable() != null) {
            attribute.setFilterable(patch.getFilterable());
        }
        return new CategoryAttributeDTO(categoryAttributeRepository.save(attribute));
    }

    @Transactional
    public SKUDTO createVariantForAdmin(Long productId, VariantCreateDTO variantDTO) {
        if (variantDTO == null || isBlank(variantDTO.getSkuCode()) || isBlank(variantDTO.getSize())
                || isBlank(variantDTO.getColor()) || variantDTO.getSellingPrice() == null
                || variantDTO.getSellingPrice().signum() < 0
                || (variantDTO.getCostPrice() != null && variantDTO.getCostPrice().signum() < 0)) {
            throw new IllegalArgumentException("Valid SKU, options, and nonnegative prices are required");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String skuCode = variantDTO.getSkuCode().trim();
        String size = variantDTO.getSize().trim();
        String color = variantDTO.getColor().trim();
        if (skuRepository.existsBySkuCodeIgnoreCase(skuCode)) {
            throw new IllegalArgumentException("SKU code already exists");
        }
        if (skuRepository.existsByProduct_ProductIdAndSizeIgnoreCaseAndColorIgnoreCase(productId, size, color)) {
            throw new IllegalArgumentException("Variant option combination already exists for this product");
        }

        SKU sku = new SKU();
        sku.setProduct(product);
        sku.setSkuCode(skuCode);
        sku.setSize(size);
        sku.setColor(color);
        SKU savedSku = skuRepository.save(sku);

        Inventory initialInventory = new Inventory();
        initialInventory.setSku(savedSku);
        initialInventory.setCostPrice(variantDTO.getCostPrice() == null ? 0 : variantDTO.getCostPrice().doubleValue());
        initialInventory.setSellingPrice(variantDTO.getSellingPrice().doubleValue());
        initialInventory.setStockAvailable(0);
        Inventory savedInventory = inventoryRepository.save(initialInventory);
        savedSku.getInventories().add(savedInventory);
        product.getSkus().add(savedSku);
        return new SKUDTO(savedSku);
    }

    @Transactional
    public SKUDTO updateVariantForAdmin(Long productId, Long variantId, VariantUpdateDTO patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Variant update is required");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        if (sku.getProduct() == null || !productId.equals(sku.getProduct().getProductId())) {
            throw new IllegalArgumentException("Variant does not belong to the specified product");
        }

        String skuCode = patch.getSkuCode() == null ? sku.getSkuCode() : patch.getSkuCode().trim();
        String size = patch.getSize() == null ? sku.getSize() : patch.getSize().trim();
        String color = patch.getColor() == null ? sku.getColor() : patch.getColor().trim();
        if (skuCode.isBlank() || size.isBlank() || color.isBlank()) {
            throw new IllegalArgumentException("SKU and variant options cannot be blank");
        }
        if (skuRepository.existsBySkuCodeIgnoreCaseAndSkuIdNot(skuCode, variantId)) {
            throw new IllegalArgumentException("SKU code already exists");
        }
        if (skuRepository.existsByProduct_ProductIdAndSizeIgnoreCaseAndColorIgnoreCaseAndSkuIdNot(
                productId, size, color, variantId)) {
            throw new IllegalArgumentException("Variant option combination already exists for this product");
        }

        List<Inventory> inventories = inventoryRepository.findBySku(sku);
        if ((patch.getSellingPrice() != null && patch.getSellingPrice().signum() < 0)
                || (patch.getCostPrice() != null && patch.getCostPrice().signum() < 0)) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if ((patch.getSellingPrice() != null || patch.getCostPrice() != null) && inventories.isEmpty()) {
            throw new IllegalStateException("Variant inventory is missing");
        }

        sku.setSkuCode(skuCode);
        sku.setSize(size);
        sku.setColor(color);
        skuRepository.save(sku);
        for (Inventory inventory : inventories) {
            if (patch.getSellingPrice() != null) {
                inventory.setSellingPrice(patch.getSellingPrice().doubleValue());
            }
            if (patch.getCostPrice() != null) {
                inventory.setCostPrice(patch.getCostPrice().doubleValue());
            }
            inventoryRepository.save(inventory);
        }
        return new SKUDTO(sku);
    }

    @Transactional
    public SKUDTO updateVariantStatusForAdmin(Long productId, Long variantId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        if (sku.getProduct() == null || !productId.equals(sku.getProduct().getProductId())) {
            throw new IllegalArgumentException("Variant does not belong to the specified product");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Variant status is required");
        }

        String normalizedStatus = status.trim().toLowerCase(Locale.ROOT);
        switch (normalizedStatus) {
            case "active", "enabled" -> sku.setActive(true);
            case "disabled", "inactive", "archived" -> sku.setActive(false);
            default -> throw new IllegalArgumentException("Unsupported variant status: " + status);
        }

        return new SKUDTO(skuRepository.save(sku));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public BrandDTO createBrandForAdmin(BrandDTO brandDTO) {
        if (brandDTO == null || brandDTO.getName() == null || brandDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Brand name is required");
        }

        String normalizedName = brandDTO.getName().trim();
        Brand existing = brandRepository.findAll().stream()
                .filter(brand -> brand.getName().equalsIgnoreCase(normalizedName))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            return new BrandDTO(existing);
        }

        Brand brand = new Brand();
        brand.setName(normalizedName);
        brand.setActive(true);
        Brand saved = brandRepository.save(brand);
        return new BrandDTO(saved);
    }

    public BrandDTO updateBrandForAdmin(Long brandId, BrandDTO brandDTO) {
        if (brandDTO == null) {
            throw new IllegalArgumentException("Brand update is required");
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (brandDTO.getName() != null) {
            if (brandDTO.getName().isBlank()) {
                throw new IllegalArgumentException("Brand name cannot be blank");
            }
            String normalizedName = brandDTO.getName().trim();
            boolean duplicate = brandRepository.findAll().stream()
                    .anyMatch(existing -> !existing.getBrandId().equals(brandId)
                            && existing.getName().equalsIgnoreCase(normalizedName));
            if (duplicate) {
                throw new IllegalArgumentException("Brand name already exists");
            }
            brand.setName(normalizedName);
        }

        brand.setActive(brandDTO.isActive());
        Brand saved = brandRepository.save(brand);
        return new BrandDTO(saved);
    }

    public CategoryDTO createCategoryForAdmin(CategoryDTO categoryDTO) {
        if (categoryDTO == null || categoryDTO.getCategory() == null || categoryDTO.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        String categoryName = categoryDTO.getCategory().trim();
        String subcategoryName = categoryDTO.getSubcategory() == null ? "" : categoryDTO.getSubcategory().trim();

        if (categoryName.equalsIgnoreCase(subcategoryName)) {
            throw new IllegalArgumentException("Category and subcategory cannot be the same");
        }

        String safeSubcategory = subcategoryName.isBlank() ? categoryName : subcategoryName;
        Category existing = categoryRepository.findByCategoryAndSubcategory(categoryName, safeSubcategory)
                .orElse(null);
        if (existing != null) {
            return new CategoryDTO(existing);
        }

        Category category = new Category();
        category.setCategory(categoryName);
        category.setSubcategory(safeSubcategory);
        Category saved = categoryRepository.save(category);
        return new CategoryDTO(saved);
    }

    public CategoryDTO updateCategoryForAdmin(String category, String subcategory, CategoryDTO patch) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        Category existing = categoryRepository.findByCategoryAndSubcategory(category.trim(), subcategory == null ? "" : subcategory.trim())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (patch != null) {
            if (patch.getCategory() != null && !patch.getCategory().isBlank()) {
                existing.setCategory(patch.getCategory().trim());
            }
            if (patch.getSubcategory() != null && !patch.getSubcategory().isBlank()) {
                existing.setSubcategory(patch.getSubcategory().trim());
            }
        }

        if (existing.getCategory().equalsIgnoreCase(existing.getSubcategory())) {
            throw new IllegalArgumentException("Category and subcategory cannot be the same");
        }

        Category saved = categoryRepository.save(existing);
        return new CategoryDTO(saved);
    }

    public void deleteCategoryForAdmin(String category, String subcategory) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        Category existing = categoryRepository.findByCategoryAndSubcategory(category.trim(), subcategory == null ? "" : subcategory.trim())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        boolean inUse = productRepository.findAll().stream()
                .anyMatch(product -> product.getCategory() != null
                        && product.getCategory().getCategory().equalsIgnoreCase(existing.getCategory())
                        && product.getCategory().getSubcategory().equalsIgnoreCase(existing.getSubcategory()));

        if (inUse) {
            throw new IllegalStateException("Category is still referenced by products and cannot be deleted");
        }

        categoryRepository.delete(existing);
    }

    public ProductDTO getProductForAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductDTO(product);
    }

    public ProductDTO updateProductStatusForAdmin(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "draft" -> {
                product.setApproved(false);
                product.setAvailable(false);
            }
            case "published", "active", "live" -> {
                product.setApproved(true);
                product.setAvailable(true);
            }
            case "unpublished", "inactive" -> {
                product.setApproved(true);
                product.setAvailable(false);
            }
            case "archived" -> {
                product.setApproved(false);
                product.setAvailable(false);
            }
            default -> throw new IllegalArgumentException("Unsupported product status: " + status);
        }

        productRepository.save(product);
        return new ProductDTO(product);
    }

    public ProductDTO updateProductForAdmin(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productDTO == null) {
            return new ProductDTO(product);
        }

        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            product.setName(productDTO.getName().trim());
            product.setSlug(CategoryDTO.slugify(productDTO.getName()));
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            product.setDescription(productDTO.getDescription().trim());
        }
        if (productDTO.getSlug() != null && !productDTO.getSlug().isBlank()) {
            product.setSlug(productDTO.getSlug().trim());
        }

        if (productDTO.getCategoryDTO() != null) {
            Category category = categoryRepository.findByCategoryAndSubcategory(
                    productDTO.getCategoryDTO().getCategory(),
                    productDTO.getCategoryDTO().getSubcategory())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCategory(productDTO.getCategoryDTO().getCategory());
                        newCategory.setSubcategory(productDTO.getCategoryDTO().getSubcategory());
                        return categoryRepository.save(newCategory);
                    });
            product.setCategory(category);
        }

        if (productDTO.getBrandName() != null && !productDTO.getBrandName().isBlank()) {
            Brand brand = brandRepository.findByActiveTrueOrderByNameAsc().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(productDTO.getBrandName().trim()))
                    .findFirst()
                    .orElseGet(() -> {
                        Brand newBrand = new Brand();
                        newBrand.setName(productDTO.getBrandName().trim());
                        newBrand.setActive(true);
                        return brandRepository.save(newBrand);
                    });
            product.setBrand(brand);
        }

        productRepository.save(product);
        return new ProductDTO(product);
    }

    public void createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDob(userDTO.getDob());
        userRepository.save(user);
    }

    public void updateUser(String userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDob(userDTO.getDob());
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public void createRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        roleRepository.save(role);
    }

    public void updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(roleDTO.getName());
        roleRepository.save(role);
    }

    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    public RoleDTO getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        return new RoleDTO(role);
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(RoleDTO::new).collect(Collectors.toList());
    }

    public void assignRoleToUser(String userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(String userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public void approveProduct(ProductDTO productDTO) {
        Product product = productRepository.findById(productDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setApproved(true);
        productRepository.save(product);
    }

    public void rejectProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public void deleteProduct(Long productId) {
        productService.deleteProduct(productId);
    }

    public void updateProduct(Long productId, ProductDTO productDTO) {
        productService.updateProduct(productId, productDTO);
    }
}
