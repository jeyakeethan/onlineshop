package com.example.onlineshop;

import com.example.onlineshop.dto.UserDTO;
import com.example.onlineshop.dto.CartDTO;
import com.example.onlineshop.dto.CartItemRequestDTO;
import com.example.onlineshop.dto.AddressDTO;
import com.example.onlineshop.dto.BrandDTO;
import com.example.onlineshop.dto.CouponDTO;
import com.example.onlineshop.dto.TaxRuleDTO;
import com.example.onlineshop.dto.ShippingZoneDTO;
import com.example.onlineshop.dto.AdminShippingMethodDTO;
import com.example.onlineshop.dto.InventoryDTO;
import com.example.onlineshop.dto.AdminInventoryDTO;
import com.example.onlineshop.dto.InventoryMovementDTO;
import com.example.onlineshop.dto.CategoryDTO;
import com.example.onlineshop.dto.CheckoutQuoteDTO;
import com.example.onlineshop.dto.CheckoutQuoteRequestDTO;
import com.example.onlineshop.dto.CouponValidationDTO;
import com.example.onlineshop.dto.CouponValidationRequestDTO;
import com.example.onlineshop.dto.BannerDTO;
import com.example.onlineshop.dto.BannerUpdateDTO;
import com.example.onlineshop.dto.FeaturedCollectionDTO;
import com.example.onlineshop.dto.FeaturedCollectionUpdateDTO;
import com.example.onlineshop.dto.ProductDTO;
import com.example.onlineshop.dto.CategoryAttributeDTO;
import com.example.onlineshop.dto.ImageUpdateDTO;
import com.example.onlineshop.dto.VariantCreateDTO;
import com.example.onlineshop.dto.VariantUpdateDTO;
import com.example.onlineshop.dto.SKUDTO;
import com.example.onlineshop.model.Address;
import com.example.onlineshop.model.CategoryAttribute;
import com.example.onlineshop.model.Brand;
import com.example.onlineshop.model.Category;
import com.example.onlineshop.model.Cart;
import com.example.onlineshop.model.CartItem;
import com.example.onlineshop.model.Banner;
import com.example.onlineshop.model.FeaturedCollection;
import com.example.onlineshop.model.Inventory;
import com.example.onlineshop.model.InventoryMovement;
import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.model.Review;
import com.example.onlineshop.model.Role;
import com.example.onlineshop.model.SKU;
import com.example.onlineshop.model.TokenSession;
import com.example.onlineshop.model.TaxRule;
import com.example.onlineshop.model.ShippingZone;
import com.example.onlineshop.model.ShippingMethod;
import com.example.onlineshop.model.User;
import com.example.onlineshop.model.WishList;
import com.example.onlineshop.repository.AddressRepository;
import com.example.onlineshop.repository.CartRepository;
import com.example.onlineshop.repository.CartItemRepository;
import com.example.onlineshop.repository.BrandRepository;
import com.example.onlineshop.repository.BannerRepository;
import com.example.onlineshop.repository.FeaturedCollectionRepository;
import com.example.onlineshop.repository.CategoryRepository;
import com.example.onlineshop.repository.CategoryAttributeRepository;
import com.example.onlineshop.repository.ProductRepository;
import com.example.onlineshop.repository.SKURepository;
import com.example.onlineshop.repository.InventoryRepository;
import com.example.onlineshop.repository.InventoryMovementRepository;
import com.example.onlineshop.repository.ImageRepository;
import com.example.onlineshop.repository.ReviewRepository;
import com.example.onlineshop.repository.RoleRepository;
import com.example.onlineshop.repository.TokenSessionRepository;
import com.example.onlineshop.repository.UserRepository;
import com.example.onlineshop.security.JwtUtil;
import com.example.onlineshop.service.AdminService;
import com.example.onlineshop.service.BuyerService;
import com.example.onlineshop.service.EmailService;
import com.example.onlineshop.service.ImageService;
import com.example.onlineshop.service.UserService;
import com.example.onlineshop.controller.UserController;
import com.example.onlineshop.util.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenSessionRepository tokenSessionRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private com.example.onlineshop.repository.WishListRepository wishListRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryAttributeRepository categoryAttributeRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BannerRepository bannerRepository;

    @Mock
    private FeaturedCollectionRepository featuredCollectionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SKURepository skuRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private com.example.onlineshop.repository.CouponRepository couponRepository;

    @Mock
    private com.example.onlineshop.repository.TaxRuleRepository taxRuleRepository;

    @Mock
    private com.example.onlineshop.repository.ShippingZoneRepository shippingZoneRepository;

    @Mock
    private com.example.onlineshop.repository.ShippingMethodRepository shippingMethodRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private BuyerService buyerService;

    @InjectMocks
    private AdminService adminService;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        lenient().when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");
    }

    @Test
    void registerUser_shouldNormalizeEmail_andRejectWeakPassword() {
        UserDTO dto = new UserDTO();
        dto.setEmail("  Alice.Smith@Example.COM  ");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("weak");
        dto.setDob("1990-01-01");
        dto.setRoles(List.of("BUYER"));

        Role buyerRole = new Role();
        buyerRole.setName("BUYER");
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.of(buyerRole));
        when(userRepository.existsById("alice.smith@example.com")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("password"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_shouldLowercaseEmailAndKeepUserUnverifiedUntilEmailConfirmed() {
        UserDTO dto = new UserDTO();
        dto.setEmail("  JOHN@EXAMPLE.COM  ");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("StrongPass!123");
        dto.setDob("1992-02-02");
        dto.setRoles(List.of("BUYER"));

        Role buyerRole = new Role();
        buyerRole.setName("BUYER");
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.of(buyerRole));
        when(userRepository.existsById("john@example.com")).thenReturn(false);

        String token = userService.registerUser(dto);

        assertEquals("jwt-token", token);
        verify(userRepository).save(argThat(user ->
                "john@example.com".equals(user.getEmail()) &&
                !user.isEmailVerified()
        ));
        verify(emailService).sendVerificationEmail(eq("john@example.com"), anyString());
    }

    @Test
    void forgotPassword_shouldGenerateResetTokenAndSendEmail() {
        User user = new User();
        user.setEmail("reset@example.com");
        user.setPassword("encoded");

        when(userRepository.findById("reset@example.com")).thenReturn(Optional.of(user));

        String result = userService.forgotPassword("reset@example.com");

        assertTrue(result.toLowerCase().contains("token generated"));
        assertNotNull(user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiresAt());
        verify(emailService).sendPasswordResetEmail(eq("reset@example.com"), anyString());
    }

    @Test
    void resendVerificationEmail_shouldIssueFreshTokenOnlyWhenAllowed() {
        User user = new User();
        user.setEmail("verify@example.com");
        user.setEmailVerified(false);
        user.setVerificationEmailSentAt(LocalDateTime.now().minusMinutes(2));

        when(userRepository.findById("verify@example.com")).thenReturn(Optional.of(user));

        String result = userService.resendVerificationEmail("verify@example.com");

        assertTrue(result.toLowerCase().contains("queued"));
        assertNotNull(user.getEmailVerificationToken());
        assertNotNull(user.getEmailVerificationTokenExpiresAt());
        verify(emailService).sendVerificationEmail(eq("verify@example.com"), anyString());
    }

    @Test
    void getAuthenticatedUser_shouldReturnOnlyPublicProfileFields() {
        User user = new User();
        user.setEmail("me@example.com");
        user.setFirstName("Me");
        user.setLastName("User");
        user.setDob("1995-04-05");
        user.setPassword("super-secret-hash");
        Role role = new Role();
        role.setName("BUYER");
        user.setRoles(List.of(role));

        when(authenticationHelper.getCurrentUserId()).thenReturn("me@example.com");
        when(userRepository.findById("me@example.com")).thenReturn(Optional.of(user));

        UserDTO profile = userService.getAuthenticatedUser();

        assertEquals("me@example.com", profile.getEmail());
        assertEquals("Me", profile.getFirstName());
        assertEquals("User", profile.getLastName());
        assertEquals("1995-04-05", profile.getDob());
        assertEquals(List.of("BUYER"), profile.getRoles());
        assertNull(profile.getPassword());
    }

    @Test
    void updateUser_shouldApplyPartialProfilePatchWithoutClearingUnspecifiedFields() {
        User user = new User();
        user.setEmail("patch@example.com");
        user.setFirstName("Original");
        user.setLastName("Name");
        user.setDob("1988-12-12");

        when(userRepository.findById("patch@example.com")).thenReturn(Optional.of(user));

        UserDTO patch = new UserDTO();
        patch.setFirstName("Updated");

        userService.updateUser("patch@example.com", patch);

        assertEquals("Updated", user.getFirstName());
        assertEquals("Name", user.getLastName());
        assertEquals("1988-12-12", user.getDob());
        verify(userRepository).save(user);
    }

    @Test
    void revokeAllSessions_shouldInvalidateEveryActiveSessionForCurrentUser() {
        String userEmail = "session-user@example.com";
        TokenSession active1 = new TokenSession();
        active1.setTokenId("t1");
        active1.setUserEmail(userEmail);
        TokenSession active2 = new TokenSession();
        active2.setTokenId("t2");
        active2.setUserEmail(userEmail);

        when(authenticationHelper.getCurrentUserId()).thenReturn(userEmail);
        when(tokenSessionRepository.findByUserEmailAndRevokedAtIsNull(userEmail)).thenReturn(List.of(active1, active2));

        String result = userService.revokeAllSessionsForCurrentUser();

        assertTrue(result.toLowerCase().contains("revoked"));
        assertNotNull(active1.getRevokedAt());
        assertNotNull(active2.getRevokedAt());
        verify(tokenSessionRepository).save(active1);
        verify(tokenSessionRepository).save(active2);
    }

    @Test
    void deactivationRequest_shouldDisableLoginAndCaptureTimestamp() {
        User user = new User();
        user.setEmail("deactivate@example.com");
        user.setActive(true);
        user.setEmailVerified(true);

        when(authenticationHelper.getCurrentUserId()).thenReturn("deactivate@example.com");
        when(userRepository.findById("deactivate@example.com")).thenReturn(Optional.of(user));

        String result = buyerService.requestAccountDeactivation();

        assertFalse(user.isActive());
        assertNotNull(user.getDeactivationRequestedAt());
        assertTrue(result.toLowerCase().contains("deactivation"));
        verify(userRepository).save(user);
    }

    @Test
    void getCurrentUserAddresses_shouldReturnOnlyOwnAddresses() {
        User user = new User();
        user.setEmail("addr@example.com");

        Address a1 = new Address();
        a1.setAddressId(1L);
        a1.setUser(user);
        a1.setStreet("123 Main St");
        a1.setCity("Austin");
        a1.setState("TX");
        a1.setCountry("US");
        a1.setPostalCode("78701");

        Address a2 = new Address();
        a2.setAddressId(2L);
        a2.setUser(user);
        a2.setStreet("5 Oak Ave");
        a2.setCity("Dallas");
        a2.setState("TX");
        a2.setCountry("US");
        a2.setPostalCode("75201");

        when(authenticationHelper.getCurrentUserId()).thenReturn("addr@example.com");
        when(userRepository.findById("addr@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByUser_Email("addr@example.com")).thenReturn(List.of(a1, a2));

        List<AddressDTO> result = buyerService.getUserAddresses();

        assertEquals(2, result.size());
        assertEquals("123 Main St", result.get(0).getStreet());
        assertEquals("addr@example.com", result.get(0).getUserId());
    }

    @Test
    void createCurrentUserAddress_shouldPersistValidAddressForTheAuthenticatedUser() {
        User user = new User();
        user.setEmail("addr-create@example.com");
        user.setActive(true);
        user.setDeleted(false);
        user.setAnonymized(false);

        AddressDTO dto = new AddressDTO();
        dto.setStreet("123 Main St");
        dto.setCity("Austin");
        dto.setState("TX");
        dto.setCountry("US");
        dto.setPostalCode("78701");

        when(authenticationHelper.getCurrentUserId()).thenReturn("addr-create@example.com");
        when(userRepository.findById("addr-create@example.com")).thenReturn(Optional.of(user));

        Address created = buyerService.createAddress(dto);

        assertEquals("123 Main St", created.getStreet());
        assertEquals("addr-create@example.com", created.getUser().getEmail());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void updateCurrentUserAddress_shouldPatchOnlyOwnAddressAndKeepOtherFields() {
        User user = new User();
        user.setEmail("addr-update@example.com");
        user.setActive(true);
        user.setDeleted(false);
        user.setAnonymized(false);

        Address address = new Address();
        address.setAddressId(9L);
        address.setUser(user);
        address.setStreet("10 Old St");
        address.setCity("Houston");
        address.setState("TX");
        address.setCountry("US");
        address.setPostalCode("77001");

        AddressDTO patch = new AddressDTO();
        patch.setStreet("20 New St");

        when(authenticationHelper.getCurrentUserId()).thenReturn("addr-update@example.com");
        when(addressRepository.findById(9L)).thenReturn(Optional.of(address));

        Address updated = buyerService.updateAddress(9L, patch);

        assertEquals("20 New St", updated.getStreet());
        assertEquals("Houston", updated.getCity());
        assertEquals("77001", updated.getPostalCode());
        verify(addressRepository).save(address);
    }

    @Test
    void deleteCurrentUserAddress_shouldDeleteOnlyOwnAddress() {
        User user = new User();
        user.setEmail("addr-delete@example.com");
        user.setActive(true);
        user.setDeleted(false);
        user.setAnonymized(false);

        Address address = new Address();
        address.setAddressId(11L);
        address.setUser(user);
        address.setStreet("88 Pine St");
        address.setCity("Seattle");
        address.setState("WA");
        address.setCountry("US");
        address.setPostalCode("98101");

        when(authenticationHelper.getCurrentUserId()).thenReturn("addr-delete@example.com");
        when(addressRepository.findById(11L)).thenReturn(Optional.of(address));

        String result = buyerService.deleteAddress(11L);

        assertTrue(result.toLowerCase().contains("deleted"));
        verify(addressRepository).delete(address);
    }

    @Test
    void setCurrentUserDefaultAddress_shouldMarkSelectedAddressAsDefaultForTheUser() {
        User user = new User();
        user.setEmail("addr-default@example.com");
        user.setActive(true);
        user.setDeleted(false);
        user.setAnonymized(false);

        Address current = new Address();
        current.setAddressId(12L);
        current.setUser(user);
        current.setStreet("44 Main St");
        current.setCity("Boston");
        current.setState("MA");
        current.setCountry("US");
        current.setPostalCode("02108");

        Address other = new Address();
        other.setAddressId(13L);
        other.setUser(user);
        other.setStreet("55 Park Ave");
        other.setCity("Boston");
        other.setState("MA");
        other.setCountry("US");
        other.setPostalCode("02109");
        other.setDefaultAddress(true);

        when(authenticationHelper.getCurrentUserId()).thenReturn("addr-default@example.com");
        when(userRepository.findById("addr-default@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findById(12L)).thenReturn(Optional.of(current));
        when(addressRepository.findByUser_Email("addr-default@example.com")).thenReturn(List.of(current, other));

        String result = buyerService.setDefaultAddress(12L);

        assertTrue(current.isDefaultAddress());
        assertFalse(other.isDefaultAddress());
        assertTrue(result.toLowerCase().contains("default"));
        verify(addressRepository).save(current);
        verify(addressRepository).save(other);
    }

    @Test
    void getCategories_shouldReturnVisibleCategoryTreeInStableOrder() {
        Category c1 = new Category();
        c1.setCategory("Electronics");
        c1.setSubcategory("Audio");

        Category c2 = new Category();
        c2.setCategory("Electronics");
        c2.setSubcategory("Phones");

        Category c3 = new Category();
        c3.setCategory("Home");
        c3.setSubcategory("Kitchen");

        when(categoryRepository.findAll()).thenReturn(List.of(c2, c3, c1));

        List<CategoryDTO> result = buyerService.getCategories();

        assertEquals(3, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        assertEquals("Audio", result.get(0).getSubcategory());
        assertEquals("Home", result.get(2).getCategory());
    }

    @Test
    void getCategoryBySlug_shouldResolveCategoryMetadataAndBreadcrumb() {
        Category c1 = new Category();
        c1.setCategory("Electronics");
        c1.setSubcategory("Audio");

        when(categoryRepository.findAll()).thenReturn(List.of(c1));

        CategoryDTO result = buyerService.getCategoryBySlug("electronics-audio");

        assertEquals("Electronics", result.getCategory());
        assertEquals("Audio", result.getSubcategory());
        assertEquals("electronics-audio", result.getSlug());
        assertEquals(List.of("Electronics", "Audio"), result.getBreadcrumb());
    }

    @Test
    void getBrands_shouldReturnOnlyActiveBrandsInNameOrder() {
        Brand active = new Brand();
        active.setBrandId(1L);
        active.setName("Zelda");
        active.setActive(true);

        Brand inactive = new Brand();
        inactive.setBrandId(2L);
        inactive.setName("Alpha");
        inactive.setActive(false);

        Brand active2 = new Brand();
        active2.setBrandId(3L);
        active2.setName("Beta");
        active2.setActive(true);

        when(brandRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(active2, active));

        List<BrandDTO> result = buyerService.getBrands();

        assertEquals(2, result.size());
        assertEquals("Beta", result.get(0).getName());
        assertEquals("Zelda", result.get(1).getName());
    }

    @Test
    void getProducts_shouldReturnOnlyActiveApprovedProductsWithSortingAndFilters() {
        Brand brand = new Brand();
        brand.setBrandId(7L);
        brand.setName("Acme");
        brand.setActive(true);

        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        Product approved = new Product();
        approved.setProductId(1L);
        approved.setName("Aural Speaker");
        approved.setApproved(true);
        approved.setBrand(brand);
        approved.setCategory(category);
        approved.setAvailable(true);

        SKU sku = new SKU();
        sku.setProduct(approved);
        Inventory inventory = new Inventory();
        inventory.setSku(sku);
        inventory.setSellingPrice(199.99d);
        inventory.setStockAvailable(6);
        sku.setInventories(List.of(inventory));
        approved.setSkus(List.of(sku));

        Product rejected = new Product();
        rejected.setProductId(2L);
        rejected.setName("Hidden Speaker");
        rejected.setApproved(false);
        rejected.setBrand(brand);
        rejected.setCategory(category);
        rejected.setAvailable(true);

        when(productRepository.findAll()).thenReturn(List.of(rejected, approved));

        List<ProductDTO> result = buyerService.getProducts("Electronics", "Acme", null, null, true, "price_asc");

        assertEquals(1, result.size());
        assertEquals("Aural Speaker", result.get(0).getName());
        assertEquals("Acme", result.get(0).getBrandName());
    }

    @Test
    void getProductBySlug_shouldResolvePublicProductFromSluggedName() {
        Product product = new Product();
        product.setProductId(15L);
        product.setName("Aural Speaker");
        product.setApproved(true);
        product.setAvailable(true);

        when(productRepository.findAll()).thenReturn(List.of(product));

        ProductDTO result = buyerService.getProductBySlug("aural-speaker");

        assertEquals(15L, result.getProductId());
        assertEquals("Aural Speaker", result.getName());
    }

    @Test
    void getSearchSuggestions_shouldReturnMatchingProductNamesForShortKeyword() {
        Product product = new Product();
        product.setProductId(55L);
        product.setName("Aural Speaker");
        product.setApproved(true);
        product.setAvailable(true);

        when(productRepository.findByNameContaining("spe")).thenReturn(List.of(product));

        List<String> result = buyerService.getSearchSuggestions("spe");

        assertEquals(1, result.size());
        assertEquals("Aural Speaker", result.get(0));
    }

    @Test
    void getSiteSearch_shouldReturnApprovedProductsMatchingQuery() {
        Product product = new Product();
        product.setProductId(77L);
        product.setName("Aural Speaker");
        product.setApproved(true);
        product.setAvailable(true);

        when(productRepository.findByNameContaining("speaker")).thenReturn(List.of(product));

        List<ProductDTO> result = buyerService.searchProducts("speaker");

        assertEquals(1, result.size());
        assertEquals("Aural Speaker", result.get(0).getName());
    }

    @Test
    void getHomepage_shouldAggregateFeaturedProductsAndCategories() {
        Product featured = new Product();
        featured.setProductId(91L);
        featured.setName("Featured Speaker");
        featured.setApproved(true);
        featured.setAvailable(true);

        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        when(productRepository.findAll()).thenReturn(List.of(featured));
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        var result = buyerService.getHomepage();

        assertEquals(1, ((List<?>) result.get("featuredProducts")).size());
        assertEquals(1, ((List<?>) result.get("categories")).size());
    }

    @Test
    void getRelatedProducts_shouldReturnSameCategoryProductsExcludingCurrentOne() {
        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        Product current = new Product();
        current.setProductId(101L);
        current.setName("Current Speaker");
        current.setApproved(true);
        current.setAvailable(true);
        current.setCategory(category);

        Product related = new Product();
        related.setProductId(102L);
        related.setName("Related Speaker");
        related.setApproved(true);
        related.setAvailable(true);
        related.setCategory(category);

        when(productRepository.findAll()).thenReturn(List.of(current, related));

        List<ProductDTO> result = buyerService.getRelatedProducts(101L);

        assertEquals(1, result.size());
        assertEquals("Related Speaker", result.get(0).getName());
    }

    @Test
    void getReviewsForProduct_shouldReturnOnlyApprovedPublicReviews() {
        Product product = new Product();
        product.setProductId(201L);
        product.setName("Review Product");

        Review publicReview = new Review();
        publicReview.setId(1L);
        publicReview.setUserId("alice@example.com");
        publicReview.setProduct(product);
        publicReview.setReviewText("Great product");
        publicReview.setRating(5);

        Review hiddenReview = new Review();
        hiddenReview.setId(2L);
        hiddenReview.setUserId("bob@example.com");
        hiddenReview.setProduct(product);
        hiddenReview.setReviewText("Hidden review");
        hiddenReview.setRating(2);

        when(productRepository.findById(201L)).thenReturn(Optional.of(product));
        when(reviewRepository.findByProductId(201L)).thenReturn(List.of(publicReview, hiddenReview));

        var result = buyerService.getProductReviews(201L);

        assertEquals(1, result.size());
        assertEquals("Great product", result.get(0).getReviewText());
    }

    @Test
    void adminProducts_shouldReturnAllProductsIncludingDraftAndArchivedStates() {
        Product product = new Product();
        product.setProductId(300L);
        product.setName("Admin Product");
        product.setApproved(true);
        product.setAvailable(false);

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = adminService.getAllProductsForAdmin();

        assertEquals(1, result.size());
        assertEquals("Admin Product", result.get(0).getName());
    }

    @Test
    void adminCreateProduct_shouldSaveDraftProductWithCoreDetails() {
        ProductDTO dto = new ProductDTO();
        dto.setName("New Audio Speaker");
        dto.setDescription("Premium compact speaker");

        String result = adminService.createProductForAdmin(dto);

        assertTrue(result.toLowerCase().contains("created"));
        verify(productRepository).save(argThat(product ->
                "New Audio Speaker".equals(product.getName()) &&
                "Premium compact speaker".equals(product.getDescription())
        ));
    }

    @Test
    void adminGetProductById_shouldReturnProductDetailForAdminCatalog() {
        Product product = new Product();
        product.setProductId(301L);
        product.setName("Admin Detail Product");
        product.setDescription("Detailed description");
        product.setApproved(false);

        when(productRepository.findById(301L)).thenReturn(Optional.of(product));

        ProductDTO result = adminService.getProductForAdmin(301L);

        assertEquals(301L, result.getProductId());
        assertEquals("Admin Detail Product", result.getName());
    }

    @Test
    void adminUpdateProduct_shouldPatchEditableProductFields() {
        Product product = new Product();
        product.setProductId(302L);
        product.setName("Old Title");
        product.setDescription("Old description");
        product.setSlug("old-title");

        ProductDTO update = new ProductDTO();
        update.setName("Updated Title");
        update.setDescription("Updated description");
        update.setSlug("updated-title");

        when(productRepository.findById(302L)).thenReturn(Optional.of(product));

        ProductDTO result = adminService.updateProductForAdmin(302L, update);

        assertEquals("Updated Title", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals("updated-title", result.getSlug());
        verify(productRepository).save(product);
    }

    @Test
    void adminUpdateProductStatus_shouldMapAllowedLifecycleStates() {
        Product product = new Product();
        product.setProductId(303L);
        product.setApproved(false);
        product.setAvailable(false);

        when(productRepository.findById(303L)).thenReturn(Optional.of(product));

        ProductDTO result = adminService.updateProductStatusForAdmin(303L, "active");

        assertTrue(result.isApproved());
        assertTrue(result.isAvailable());
        verify(productRepository).save(product);
    }

    @Test
    void adminCategories_shouldReturnAllCategoryTreeEntries() {
        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDTO> result = adminService.getAllCategoriesForAdmin();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        assertEquals("Audio", result.get(0).getSubcategory());
    }

    @Test
    void adminCreateCategory_shouldPersistUniqueCategoryWithParentAndSlug() {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory("Electronics");
        dto.setSubcategory("Audio");

        CategoryDTO result = adminService.createCategoryForAdmin(dto);

        assertEquals("Electronics", result.getCategory());
        assertEquals("Audio", result.getSubcategory());
        verify(categoryRepository).save(argThat(category ->
                "Electronics".equals(category.getCategory()) &&
                "Audio".equals(category.getSubcategory())
        ));
    }

    @Test
    void adminUpdateCategory_shouldRenameCategoryAndSubcategoryWithoutBreakingSlug() {
        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        CategoryDTO patch = new CategoryDTO();
        patch.setCategory("Devices");
        patch.setSubcategory("Audio");

        when(categoryRepository.findByCategoryAndSubcategory("Electronics", "Audio")).thenReturn(Optional.of(category));

        CategoryDTO result = adminService.updateCategoryForAdmin("Electronics", "Audio", patch);

        assertEquals("Devices", result.getCategory());
        assertEquals("Audio", result.getSubcategory());
        verify(categoryRepository).save(category);
    }

    @Test
    void adminDeleteCategory_shouldRejectRemovalWhenProductsStillReferenceIt() {
        Category category = new Category();
        category.setCategory("Electronics");
        category.setSubcategory("Audio");

        Product product = new Product();
        product.setProductId(304L);
        product.setCategory(category);

        when(categoryRepository.findByCategoryAndSubcategory("Electronics", "Audio")).thenReturn(Optional.of(category));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThrows(IllegalStateException.class, () -> adminService.deleteCategoryForAdmin("Electronics", "Audio"));
    }

    @Test
    void adminBrands_shouldReturnAllBrandsIncludingInactiveEntries() {
        Brand brand = new Brand();
        brand.setBrandId(401L);
        brand.setName("Acme");
        brand.setActive(false);

        when(brandRepository.findAll()).thenReturn(List.of(brand));

        List<BrandDTO> result = adminService.getAllBrandsForAdmin();

        assertEquals(1, result.size());
        assertEquals("Acme", result.get(0).getName());
    }

    @Test
    void adminCreateBrand_shouldPersistNormalizedBrandName() {
        BrandDTO dto = new BrandDTO();
        dto.setName("  Acme  ");

        BrandDTO result = adminService.createBrandForAdmin(dto);

        assertEquals("Acme", result.getName());
        verify(brandRepository).save(argThat(brand -> "Acme".equals(brand.getName())));
    }

    @Test
    void adminUpdateBrand_shouldEditExistingBrandWithoutReplacingEntity() {
        Brand brand = new Brand();
        brand.setBrandId(402L);
        brand.setName("Acme");
        brand.setActive(true);

        BrandDTO patch = new BrandDTO();
        patch.setName("Acme Audio");
        patch.setActive(false);

        when(brandRepository.findById(402L)).thenReturn(Optional.of(brand));

        BrandDTO result = adminService.updateBrandForAdmin(402L, patch);

        assertEquals("Acme Audio", result.getName());
        assertFalse(result.isActive());
        verify(brandRepository).save(brand);
    }

    @Test
    void adminAttributes_shouldListCategoryDefinitionsWithValuesAndFilterability() {
        CategoryAttribute attribute = new CategoryAttribute();
        attribute.setId(501L);
        attribute.setCategory("Electronics");
        attribute.setSubcategory("Audio");
        attribute.setName("Connectivity");
        attribute.setDataType("ENUM");
        attribute.setAllowedValues(List.of("Bluetooth", "Wired"));
        attribute.setFilterable(true);

        when(categoryAttributeRepository.findAllByOrderByCategoryAscSubcategoryAscNameAsc())
                .thenReturn(List.of(attribute));

        List<CategoryAttributeDTO> result = adminService.getAllCategoryAttributesForAdmin();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        assertEquals(List.of("Bluetooth", "Wired"), result.get(0).getAllowedValues());
        assertTrue(result.get(0).isFilterable());
    }

    @Test
    void adminCreateAttribute_shouldValidateAndPersistTypedDefinition() {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setCategory("Electronics");
        dto.setSubcategory("Audio");
        dto.setName("Connectivity");
        dto.setDataType("ENUM");
        dto.setAllowedValues(List.of("Bluetooth", "Wired"));
        dto.setFilterable(true);

        when(categoryAttributeRepository.existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCase(
                "Electronics", "Audio", "Connectivity")).thenReturn(false);

        CategoryAttributeDTO result = adminService.createCategoryAttributeForAdmin(dto);

        assertEquals("ENUM", result.getDataType());
        assertEquals(List.of("Bluetooth", "Wired"), result.getAllowedValues());
        verify(categoryAttributeRepository).save(argThat(attribute ->
                "Electronics".equals(attribute.getCategory()) &&
                "Connectivity".equals(attribute.getName()) &&
                attribute.isFilterable()
        ));
    }

    @Test
    void adminUpdateAttribute_shouldUpdateExistingSchemaDefinitionInPlace() {
        CategoryAttribute attribute = new CategoryAttribute();
        attribute.setId(502L);
        attribute.setCategory("Electronics");
        attribute.setSubcategory("Audio");
        attribute.setName("Connectivity");
        attribute.setDataType("ENUM");
        attribute.setAllowedValues(List.of("Bluetooth", "Wired"));
        attribute.setFilterable(true);

        CategoryAttributeDTO patch = new CategoryAttributeDTO();
        patch.setName("Connection type");
        patch.setDataType("ENUM");
        patch.setAllowedValues(List.of("Bluetooth", "Wi-Fi"));
        patch.setFilterable(false);

        when(categoryAttributeRepository.findById(502L)).thenReturn(Optional.of(attribute));
        when(categoryAttributeRepository.existsByCategoryIgnoreCaseAndSubcategoryIgnoreCaseAndNameIgnoreCaseAndIdNot(
                "Electronics", "Audio", "Connection type", 502L)).thenReturn(false);

        CategoryAttributeDTO result = adminService.updateCategoryAttributeForAdmin(502L, patch);

        assertEquals("Connection type", result.getName());
        assertEquals(List.of("Bluetooth", "Wi-Fi"), result.getAllowedValues());
        assertFalse(result.isFilterable());
        verify(categoryAttributeRepository).save(attribute);
    }

    @Test
    void adminCreateVariant_shouldPersistUniqueSkuAndZeroStockInventory() {
        Product product = new Product();
        product.setProductId(601L);

        VariantCreateDTO dto = new VariantCreateDTO();
        dto.setSkuCode("AUDIO-BLK-S");
        dto.setSize("S");
        dto.setColor("Black");
        dto.setSellingPrice(new java.math.BigDecimal("29.99"));

        when(productRepository.findById(601L)).thenReturn(Optional.of(product));
        when(skuRepository.existsBySkuCodeIgnoreCase("AUDIO-BLK-S")).thenReturn(false);
        when(skuRepository.existsByProduct_ProductIdAndSizeIgnoreCaseAndColorIgnoreCase(601L, "S", "Black"))
                .thenReturn(false);
        when(skuRepository.save(any(SKU.class))).thenAnswer(invocation -> {
            SKU sku = invocation.getArgument(0);
            sku.setSkuId(602L);
            return sku;
        });
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SKUDTO result = adminService.createVariantForAdmin(601L, dto);

        assertEquals("AUDIO-BLK-S", result.getSkuCode());
        verify(inventoryRepository).save(argThat(inventory -> inventory.getStockAvailable() == 0
                && inventory.getSellingPrice() == 29.99));
    }

    @Test
    void adminUpdateVariant_shouldValidateOwnershipAndUpdateSkuAndPrice() {
        Product product = new Product();
        product.setProductId(611L);
        SKU sku = new SKU();
        sku.setSkuId(612L);
        sku.setProduct(product);
        sku.setSkuCode("OLD-SKU");
        sku.setSize("S");
        sku.setColor("Black");
        Inventory inventory = new Inventory();
        inventory.setInventoryId(613L);
        inventory.setSku(sku);
        inventory.setSellingPrice(20.0);
        inventory.setCostPrice(10.0);
        sku.setInventories(new java.util.ArrayList<>(List.of(inventory)));

        VariantUpdateDTO patch = new VariantUpdateDTO();
        patch.setSkuCode("NEW-SKU");
        patch.setSize("M");
        patch.setSellingPrice(new java.math.BigDecimal("24.50"));

        when(productRepository.findById(611L)).thenReturn(Optional.of(product));
        when(skuRepository.findById(612L)).thenReturn(Optional.of(sku));
        when(skuRepository.existsBySkuCodeIgnoreCaseAndSkuIdNot("NEW-SKU", 612L)).thenReturn(false);
        when(skuRepository.existsByProduct_ProductIdAndSizeIgnoreCaseAndColorIgnoreCaseAndSkuIdNot(
                611L, "M", "Black", 612L)).thenReturn(false);
        when(skuRepository.save(sku)).thenReturn(sku);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        SKUDTO result = adminService.updateVariantForAdmin(611L, 612L, patch);

        assertEquals("NEW-SKU", result.getSkuCode());
        assertEquals("M", result.getSize());
        assertEquals(24.5, inventory.getSellingPrice());
        verify(skuRepository).save(sku);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void adminUpdateVariantStatus_shouldDeactivateWithoutDeletingInventoryHistory() {
        Product product = new Product();
        product.setProductId(621L);
        SKU sku = new SKU();
        sku.setSkuId(622L);
        sku.setProduct(product);
        sku.setActive(true);
        sku.setInventories(new java.util.ArrayList<>());

        when(productRepository.findById(621L)).thenReturn(Optional.of(product));
        when(skuRepository.findById(622L)).thenReturn(Optional.of(sku));
        when(skuRepository.save(sku)).thenReturn(sku);

        SKUDTO result = adminService.updateVariantStatusForAdmin(621L, 622L, "disabled");

        assertFalse(result.isActive());
        assertTrue(sku.getInventories().isEmpty());
        verify(skuRepository).save(sku);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void adminUploadProductImage_shouldValidateSignatureAndAttachToProduct() throws Exception {
        Product product = new Product();
        product.setProductId(631L);
        byte[] pngSignature = new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a};
        MockMultipartFile file = new MockMultipartFile("file", "product photo.png", "text/plain", pngSignature);
        when(productRepository.findById(631L)).thenReturn(Optional.of(product));
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> {
            Image image = invocation.getArgument(0);
            image.setImageId(632L);
            return image;
        });

        Image result = imageService.saveProductImage(631L, file);

        assertEquals("image/png", result.getImageType());
        assertEquals("product_photo.png", result.getImageName());
        assertSame(product, result.getProduct());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void adminUploadProductImage_shouldRejectInvalidContentBeforePersistence() {
        MockMultipartFile file = new MockMultipartFile("file", "payload.png", "image/png", "not an image".getBytes());

        assertThrows(IllegalArgumentException.class, () -> imageService.saveProductImage(631L, file));
        verifyNoInteractions(imageRepository);
        verifyNoInteractions(productRepository);
    }

    @Test
    void adminUpdateProductImage_shouldKeepOnlyOnePrimaryImage() {
        Product product = new Product();
        product.setProductId(641L);
        Image primary = new Image();
        primary.setImageId(642L);
        primary.setProduct(product);
        primary.setPrimaryImage(true);
        Image target = new Image();
        target.setImageId(643L);
        target.setProduct(product);
        target.setPrimaryImage(false);
        ImageUpdateDTO patch = new ImageUpdateDTO();
        patch.setAltText("Side view");
        patch.setDisplayOrder(2);
        patch.setPrimaryImage(true);

        when(imageRepository.findByImageIdAndProduct_ProductId(643L, 641L)).thenReturn(Optional.of(target));
        when(imageRepository.findByProduct_ProductIdOrderByDisplayOrderAscImageIdAsc(641L))
                .thenReturn(List.of(primary, target));
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.updateProductImage(641L, 643L, patch);

        assertEquals("Side view", result.getAltText());
        assertEquals(2, result.getDisplayOrder());
        assertTrue(result.isPrimaryImage());
        assertFalse(primary.isPrimaryImage());
        verify(imageRepository).save(primary);
        verify(imageRepository).save(target);
    }

    @Test
    void adminDeleteProductImage_shouldPromoteNextImageWhenDeletingPrimary() {
        Product product = new Product();
        product.setProductId(651L);
        Image primary = new Image();
        primary.setImageId(652L);
        primary.setProduct(product);
        primary.setPrimaryImage(true);
        Image next = new Image();
        next.setImageId(653L);
        next.setProduct(product);
        next.setDisplayOrder(1);
        next.setPrimaryImage(false);

        when(imageRepository.findByImageIdAndProduct_ProductId(652L, 651L)).thenReturn(Optional.of(primary));
        when(imageRepository.findByProduct_ProductIdOrderByDisplayOrderAscImageIdAsc(651L))
                .thenReturn(List.of(primary, next));

        imageService.deleteProductImage(651L, 652L);

        assertTrue(next.isPrimaryImage());
        verify(imageRepository).delete(primary);
        verify(imageRepository).save(next);
    }

    @Test
    void adminDeleteProductImage_shouldRejectImageBelongingToAnotherProduct() {
        when(imageRepository.findByImageIdAndProduct_ProductId(662L, 661L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> imageService.deleteProductImage(661L, 662L));

        verify(imageRepository, never()).delete(any(Image.class));
    }

    @Test
    void adminBanners_shouldListSchedulingMetadataIncludingInactiveEntries() {
        Banner banner = new Banner();
        banner.setBannerId(671L);
        banner.setTitle("Spring sale");
        banner.setMediaUrl("/media/spring-sale.jpg");
        banner.setLinkUrl("/sale");
        banner.setDisplayOrder(2);
        banner.setActive(false);
        banner.setStartsAt(LocalDateTime.parse("2026-03-01T00:00:00"));
        banner.setEndsAt(LocalDateTime.parse("2026-03-31T23:59:59"));
        banner.setArchived(true);

        when(bannerRepository.findAllByOrderByDisplayOrderAscBannerIdAsc()).thenReturn(List.of(banner));

        List<BannerDTO> result = adminService.getAllBannersForAdmin();

        assertEquals(1, result.size());
        assertEquals("Spring sale", result.get(0).getTitle());
        assertFalse(result.get(0).isActive());
        assertTrue(result.get(0).isArchived());
        assertEquals(banner.getStartsAt(), result.get(0).getStartsAt());
    }

    @Test
    void adminCreateBanner_shouldPersistValidatedContentAndSchedule() {
        BannerDTO dto = new BannerDTO();
        dto.setTitle("Summer sale");
        dto.setMediaUrl("/media/summer-sale.webp");
        dto.setLinkUrl("/collections/summer");
        dto.setDisplayOrder(3);
        dto.setActive(true);
        dto.setStartsAt(LocalDateTime.parse("2026-06-01T00:00:00"));
        dto.setEndsAt(LocalDateTime.parse("2026-06-30T23:59:59"));

        when(bannerRepository.save(any(Banner.class))).thenAnswer(invocation -> {
            Banner banner = invocation.getArgument(0);
            banner.setBannerId(672L);
            return banner;
        });

        BannerDTO result = adminService.createBannerForAdmin(dto);

        assertEquals(672L, result.getBannerId());
        assertEquals("/media/summer-sale.webp", result.getMediaUrl());
        assertEquals("/collections/summer", result.getLinkUrl());
        assertEquals(3, result.getDisplayOrder());
        assertTrue(result.isActive());
        verify(bannerRepository).save(any(Banner.class));
    }

    @Test
    void adminCreateBanner_shouldRejectUnsafeLinkAndInvalidSchedule() {
        BannerDTO unsafeLink = new BannerDTO();
        unsafeLink.setTitle("Unsafe");
        unsafeLink.setMediaUrl("/media/banner.webp");
        unsafeLink.setLinkUrl("javascript:alert(1)");
        assertThrows(IllegalArgumentException.class, () -> adminService.createBannerForAdmin(unsafeLink));

        BannerDTO invalidSchedule = new BannerDTO();
        invalidSchedule.setTitle("Invalid schedule");
        invalidSchedule.setMediaUrl("/media/banner.webp");
        invalidSchedule.setStartsAt(LocalDateTime.parse("2026-07-02T00:00:00"));
        invalidSchedule.setEndsAt(LocalDateTime.parse("2026-07-01T00:00:00"));
        assertThrows(IllegalArgumentException.class, () -> adminService.createBannerForAdmin(invalidSchedule));

        verify(bannerRepository, never()).save(any(Banner.class));
    }

    @Test
    void adminUpdateBanner_shouldPatchPublicationAndOrderWithoutChangingOmittedFields() {
        Banner banner = new Banner();
        banner.setBannerId(681L);
        banner.setTitle("Existing banner");
        banner.setMediaUrl("/media/existing.webp");
        banner.setLinkUrl("/existing");
        banner.setDisplayOrder(1);
        banner.setActive(false);

        BannerUpdateDTO patch = new BannerUpdateDTO();
        patch.setDisplayOrder(4);
        patch.setActive(true);

        when(bannerRepository.findById(681L)).thenReturn(Optional.of(banner));
        when(bannerRepository.save(banner)).thenReturn(banner);

        BannerDTO result = adminService.updateBannerForAdmin(681L, patch);

        assertTrue(result.isActive());
        assertEquals(4, result.getDisplayOrder());
        assertEquals("Existing banner", result.getTitle());
        assertEquals("/media/existing.webp", result.getMediaUrl());
        verify(bannerRepository).save(banner);
    }

    @Test
    void adminDeleteBanner_shouldArchiveWithoutDeletingContentReferences() {
        Banner banner = new Banner();
        banner.setBannerId(691L);
        banner.setTitle("Keep reference");
        banner.setMediaUrl("/media/kept.webp");
        banner.setLinkUrl("/kept");
        banner.setActive(true);

        when(bannerRepository.findById(691L)).thenReturn(Optional.of(banner));
        when(bannerRepository.save(banner)).thenReturn(banner);

        adminService.archiveBannerForAdmin(691L);

        assertTrue(banner.isArchived());
        assertFalse(banner.isActive());
        assertEquals("/media/kept.webp", banner.getMediaUrl());
        assertEquals("/kept", banner.getLinkUrl());
        verify(bannerRepository).save(banner);
        verify(bannerRepository, never()).delete(any(Banner.class));
    }

    @Test
    void adminCollections_shouldListVisibilityOrderAndProductMemberships() {
        Product first = new Product();
        first.setProductId(701L);
        first.setName("First product");
        Product second = new Product();
        second.setProductId(702L);
        second.setName("Second product");

        FeaturedCollection collection = new FeaturedCollection();
        collection.setCollectionId(703L);
        collection.setName("Summer picks");
        collection.setSlug("summer-picks");
        collection.setActive(false);
        collection.setDisplayOrder(5);
        collection.setProducts(List.of(first, second));

        when(featuredCollectionRepository.findAllByOrderByDisplayOrderAscCollectionIdAsc())
                .thenReturn(List.of(collection));

        List<FeaturedCollectionDTO> result = adminService.getAllCollectionsForAdmin();

        assertEquals(1, result.size());
        assertEquals("Summer picks", result.get(0).getName());
        assertFalse(result.get(0).isActive());
        assertEquals(5, result.get(0).getDisplayOrder());
        assertEquals(List.of(701L, 702L), result.get(0).getProductIds());
    }

    @Test
    void adminCreateCollection_shouldPersistCuratedProductOrder() {
        Product first = new Product();
        first.setProductId(711L);
        Product second = new Product();
        second.setProductId(712L);

        FeaturedCollectionDTO dto = new FeaturedCollectionDTO();
        dto.setName("Summer Picks");
        dto.setDisplayOrder(2);
        dto.setActive(true);
        dto.setProductIds(List.of(712L, 711L));

        when(productRepository.findAllById(List.of(712L, 711L))).thenReturn(List.of(first, second));
        when(featuredCollectionRepository.save(any(FeaturedCollection.class))).thenAnswer(invocation -> {
            FeaturedCollection collection = invocation.getArgument(0);
            collection.setCollectionId(713L);
            return collection;
        });

        FeaturedCollectionDTO result = adminService.createCollectionForAdmin(dto);

        assertEquals("summer-picks", result.getSlug());
        assertEquals(List.of(712L, 711L), result.getProductIds());
        assertEquals(2, result.getDisplayOrder());
        verify(featuredCollectionRepository).save(any(FeaturedCollection.class));
    }

    @Test
    void adminCreateCollection_shouldRejectUnknownProductMembership() {
        FeaturedCollectionDTO dto = new FeaturedCollectionDTO();
        dto.setName("Summer Picks");
        dto.setProductIds(List.of(799L));
        when(productRepository.findAllById(List.of(799L))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> adminService.createCollectionForAdmin(dto));
        verify(featuredCollectionRepository, never()).save(any(FeaturedCollection.class));
    }

    @Test
    void adminUpdateCollection_shouldPatchMembershipVisibilityAndOrder() {
        Product retained = new Product();
        retained.setProductId(721L);
        Product added = new Product();
        added.setProductId(722L);
        FeaturedCollection collection = new FeaturedCollection();
        collection.setCollectionId(723L);
        collection.setName("Staff picks");
        collection.setSlug("staff-picks");
        collection.setActive(false);
        collection.setDisplayOrder(1);
        collection.setProducts(new java.util.ArrayList<>(List.of(retained)));

        FeaturedCollectionUpdateDTO patch = new FeaturedCollectionUpdateDTO();
        patch.setActive(true);
        patch.setDisplayOrder(4);
        patch.setProductIds(List.of(722L, 721L));

        when(featuredCollectionRepository.findById(723L)).thenReturn(Optional.of(collection));
        when(productRepository.findAllById(List.of(722L, 721L))).thenReturn(List.of(retained, added));
        when(featuredCollectionRepository.save(collection)).thenReturn(collection);

        FeaturedCollectionDTO result = adminService.updateCollectionForAdmin(723L, patch);

        assertTrue(result.isActive());
        assertEquals(4, result.getDisplayOrder());
        assertEquals(List.of(722L, 721L), result.getProductIds());
        verify(featuredCollectionRepository).save(collection);
    }

    @Test
    void adminInventory_shouldReturnOnHandReservedAvailableAndLowStock() {
        Product product = new Product();
        product.setProductId(731L);
        SKU sku = new SKU();
        sku.setSkuId(732L);
        sku.setSkuCode("INVENTORY-732");
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(733L);
        inventory.setSku(sku);
        inventory.setStockAvailable(4);
        inventory.setReservedQuantity(2);
        inventory.setLowStockThreshold(5);
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        List<AdminInventoryDTO> result = adminService.getAdminInventory();

        assertEquals(1, result.size());
        assertEquals(6, result.get(0).getOnHand());
        assertEquals(2, result.get(0).getReserved());
        assertEquals(4, result.get(0).getAvailable());
        assertTrue(result.get(0).isLowStock());
    }

    @Test
    void adminInventoryMovements_shouldReturnPagedLedgerWithActorAndReason() {
        Product product = new Product();
        product.setProductId(741L);
        SKU sku = new SKU();
        sku.setSkuId(742L);
        sku.setSkuCode("MOVEMENT-742");
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(743L);
        inventory.setSku(sku);
        InventoryMovement movement = new InventoryMovement();
        movement.setMovementId(744L);
        movement.setInventory(inventory);
        movement.setQuantityDelta(3);
        movement.setReason("Count correction");
        movement.setActorId("admin@example.com");
        movement.setReference("cycle-count-11");
        movement.setOccurredAt(LocalDateTime.parse("2026-10-20T10:15:00"));
        when(skuRepository.findById(742L)).thenReturn(Optional.of(sku));
        when(inventoryMovementRepository.findByInventory_Sku_SkuIdOrderByOccurredAtDescMovementIdDesc(
                eq(742L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(movement)));

        org.springframework.data.domain.Page<InventoryMovementDTO> result =
                adminService.getInventoryMovements(742L, 0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("Count correction", result.getContent().get(0).getReason());
        assertEquals("admin@example.com", result.getContent().get(0).getActorId());
        assertEquals("cycle-count-11", result.getContent().get(0).getReference());
    }

        @Test
        void adminAdjustInventory_shouldUpdateAvailableAndAppendAuditedMovement() {
        Product product = new Product();
        product.setProductId(751L);
        SKU sku = new SKU();
        sku.setSkuId(752L);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(753L);
        inventory.setSku(sku);
        inventory.setStockAvailable(5);
        inventory.setReservedQuantity(2);
        when(skuRepository.findById(752L)).thenReturn(Optional.of(sku));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));
        when(authenticationHelper.getCurrentUserId()).thenReturn("admin@example.com");
        when(inventoryMovementRepository.save(any(InventoryMovement.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryDTO result = adminService.adjustInventory(752L, 3, "Cycle count", "count-2026-01");

        assertEquals(8, result.getStockAvailable());
        verify(inventoryRepository).save(inventory);
        verify(inventoryMovementRepository).save(argThat(movement ->
            movement.getQuantityDelta() == 3 &&
            "Cycle count".equals(movement.getReason()) &&
            "admin@example.com".equals(movement.getActorId()) &&
            "count-2026-01".equals(movement.getReference())
        ));
        }

        @Test
        void adminAdjustInventory_shouldRejectNegativeAvailableAndMissingReason() {
        Product product = new Product();
        SKU sku = new SKU();
        sku.setSkuId(762L);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(763L);
        inventory.setSku(sku);
        inventory.setStockAvailable(2);
        inventory.setReservedQuantity(1);
        when(skuRepository.findById(762L)).thenReturn(Optional.of(sku));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));

        assertThrows(IllegalArgumentException.class,
            () -> adminService.adjustInventory(762L, -2, "  ", null));
        assertThrows(IllegalArgumentException.class,
            () -> adminService.adjustInventory(762L, -2, "Damaged stock", null));

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(inventoryMovementRepository, never()).save(any(InventoryMovement.class));
        }

    @Test
    void adminSetInventoryThreshold_shouldPersistNonnegativeThreshold() {
        SKU sku = new SKU();
        sku.setSkuId(771L);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(772L);
        inventory.setSku(sku);
        inventory.setStockAvailable(8);
        when(skuRepository.findById(771L)).thenReturn(Optional.of(sku));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));

        InventoryDTO result = adminService.setInventoryThreshold(771L, 4);

        assertEquals(4, inventory.getLowStockThreshold());
        assertEquals(4, result.getLowStockThreshold());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void adminSetInventoryThreshold_shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> adminService.setInventoryThreshold(771L, -1));
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void adminLowStockInventory_shouldReturnOnlyPagedRowsAtOrBelowThreshold() {
        Product product = new Product();
        SKU sku = new SKU();
        sku.setSkuId(781L);
        sku.setSkuCode("LOW-781");
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(782L);
        inventory.setSku(sku);
        inventory.setStockAvailable(3);
        inventory.setLowStockThreshold(5);
        when(inventoryRepository.findLowStock(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(inventory)));

        org.springframework.data.domain.Page<AdminInventoryDTO> result =
                adminService.getLowStockInventory(0, 10);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).isLowStock());
        assertEquals(3, result.getContent().get(0).getAvailable());
    }

    @Test
    void getCart_shouldExposeCurrentPriceAvailabilityAndStockWarnings() {
        User user = new User();
        user.setEmail("cart-owner@example.com");
        Product product = new Product();
        product.setProductId(791L);
        product.setName("Cart speaker");
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setSkuId(792L);
        sku.setSkuCode("CART-792");
        sku.setActive(true);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(793L);
        inventory.setSku(sku);
        inventory.setSellingPrice(19.95);
        inventory.setStockAvailable(2);
        Cart cart = new Cart();
        cart.setCartId(794L);
        cart.setUser(user);
        CartItem line = new CartItem(cart, inventory, 3);
        line.setCartItemId(795L);
        cart.setCartItems(List.of(line));
        when(cartRepository.findByUserEmail("cart-owner@example.com")).thenReturn(cart);

        CartDTO result = buyerService.getCart("cart-owner@example.com");

        assertEquals("cart-owner@example.com", result.getUserId());
        assertEquals(19.95, result.getCartItems().get(0).getCurrentPrice());
        assertEquals(2, result.getCartItems().get(0).getAvailableQuantity());
        assertTrue(result.getCartItems().get(0).isStockWarning());
        assertFalse(result.getCartItems().get(0).isUnavailable());
    }

    @Test
    void getCart_shouldReturnEmptyCartWhenAccountHasNoCart() {
        when(cartRepository.findByUserEmail("new-owner@example.com")).thenReturn(null);

        CartDTO result = buyerService.getCart("new-owner@example.com");

        assertEquals("new-owner@example.com", result.getUserId());
        assertTrue(result.getCartItems().isEmpty());
    }

    @Test
    void addCartItem_shouldMergeDuplicateVariantAndEnforcePurchaseLimit() {
        User user = new User();
        user.setEmail("cart-owner@example.com");
        Cart cart = new Cart();
        cart.setCartId(801L);
        cart.setUser(user);
        cart.setCartItems(new java.util.ArrayList<>());
        Product product = new Product();
        product.setProductId(802L);
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setSkuId(803L);
        sku.setActive(true);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(804L);
        inventory.setSku(sku);
        inventory.setStockAvailable(10);
        CartItem existing = new CartItem(cart, inventory, 2);
        existing.setCartItemId(805L);
        cart.getCartItems().add(existing);
        when(authenticationHelper.getCurrentUserId()).thenReturn("cart-owner@example.com");
        when(cartRepository.findByUserEmail("cart-owner@example.com")).thenReturn(cart);
        when(inventoryRepository.findById(804L)).thenReturn(Optional.of(inventory));
        when(cartItemRepository.save(existing)).thenReturn(existing);

        buyerService.addProductToCart(804L, 3);

        assertEquals(5, existing.getQuantity());
        verify(cartItemRepository).save(existing);
        assertThrows(IllegalArgumentException.class, () -> buyerService.addProductToCart(804L, 6));
    }

    @Test
    void updateCartItemQuantity_shouldCheckOwnershipStockAndPersistNewQuantity() {
        User owner = new User();
        owner.setEmail("cart-owner@example.com");
        Cart cart = new Cart();
        cart.setCartId(811L);
        cart.setUser(owner);
        Product product = new Product();
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setActive(true);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(812L);
        inventory.setSku(sku);
        inventory.setStockAvailable(8);
        CartItem item = new CartItem(cart, inventory, 2);
        item.setCartItemId(813L);
        when(authenticationHelper.getCurrentUserId()).thenReturn("cart-owner@example.com");
        when(cartItemRepository.findById(813L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(item)).thenReturn(item);

        buyerService.updateCartItemQuantity(813L, 5);

        assertEquals(5, item.getQuantity());
        verify(cartItemRepository).save(item);

        assertThrows(IllegalArgumentException.class, () -> buyerService.updateCartItemQuantity(813L, 9));
    }

    @Test
    void deleteCartItem_shouldDeleteOnlyWhenOwnedByCurrentCart() {
        User owner = new User();
        owner.setEmail("cart-owner@example.com");
        Cart cart = new Cart();
        cart.setCartId(821L);
        cart.setUser(owner);
        CartItem item = new CartItem();
        item.setCartItemId(822L);
        item.setCart(cart);
        when(authenticationHelper.getCurrentUserId()).thenReturn("cart-owner@example.com");
        when(cartItemRepository.findById(822L)).thenReturn(Optional.of(item));

        buyerService.deleteCartItem(822L);

        verify(cartItemRepository).delete(item);

        User other = new User();
        other.setEmail("other@example.com");
        Cart otherCart = new Cart();
        otherCart.setUser(other);
        item.setCart(otherCart);
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.deleteCartItem(822L));
        verify(cartItemRepository, times(1)).delete(item);
    }

    @Test
    void clearCurrentCart_shouldBeIdempotentAndLeaveInventoryUntouched() {
        User owner = new User();
        owner.setEmail("cart-owner@example.com");
        Cart cart = new Cart();
        cart.setCartId(831L);
        cart.setUser(owner);
        Product product = new Product();
        SKU sku = new SKU();
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(832L);
        inventory.setSku(sku);
        CartItem item = new CartItem(cart, inventory, 2);
        cart.setCartItems(new java.util.ArrayList<>(List.of(item)));
        when(authenticationHelper.getCurrentUserId()).thenReturn("cart-owner@example.com");
        when(cartRepository.findByUserEmail("cart-owner@example.com")).thenReturn(cart).thenReturn(null);

        buyerService.clearCurrentCart();
        buyerService.clearCurrentCart();

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartItemRepository).deleteAll(anyList());
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void mergeGuestCart_shouldMergeByVariantAndRotateToken() {
        User owner = new User();
        owner.setEmail("cart-owner@example.com");
        Cart cart = new Cart();
        cart.setCartId(841L);
        cart.setUser(owner);
        cart.setCartItems(new java.util.ArrayList<>());
        Product product = new Product();
        product.setProductId(842L);
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setSkuId(843L);
        sku.setActive(true);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(844L);
        inventory.setSku(sku);
        inventory.setStockAvailable(8);
        when(authenticationHelper.getCurrentUserId()).thenReturn("cart-owner@example.com");
        when(userRepository.findById("cart-owner@example.com")).thenReturn(Optional.of(owner));
        when(cartRepository.findByUserEmail("cart-owner@example.com")).thenReturn(cart); 
        when(skuRepository.findById(843L)).thenReturn(Optional.of(sku));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));
        when(inventoryRepository.findById(844L)).thenReturn(Optional.of(inventory));

        CartItemRequestDTO guestItem = new CartItemRequestDTO();
        guestItem.setVariantId(843L);
        guestItem.setQuantity(3);

        String rotatedToken = buyerService.mergeGuestCart("guest-token-1", List.of(guestItem));

        assertNotNull(rotatedToken);
        assertNotEquals("guest-token-1", rotatedToken);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(3, cart.getCartItems().get(0).getQuantity());
        assertThrows(IllegalStateException.class,
                () -> buyerService.mergeGuestCart("guest-token-1", List.of(guestItem)));
    }

    @Test
    void getWishlist_shouldReturnAuthenticatedWishlistItems() {
        User user = new User();
        user.setEmail("shopper@example.com");
        WishList wishlist = new WishList();
        wishlist.setWishlistId(851L);
        wishlist.setUser(user);
        Product product = new Product();
        product.setProductId(852L);
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setSkuId(853L);
        sku.setProduct(product);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(854L);
        inventory.setSku(sku);
        inventory.setSellingPrice(24.99);
        wishlist.setInventories(List.of(inventory));
        when(authenticationHelper.getCurrentUserId()).thenReturn("shopper@example.com");
        when(wishListRepository.findByUserEmail("shopper@example.com")).thenReturn(Optional.of(wishlist));

        List<InventoryDTO> result = buyerService.getWishlist();

        assertEquals(1, result.size());
        assertEquals(854L, result.get(0).getInventoryId());
    }

    @Test
    void wishlistOperations_shouldBeIdempotentAndMoveToCartOnSuccess() {
        User user = new User();
        user.setEmail("shopper@example.com");
        WishList wishlist = new WishList();
        wishlist.setWishlistId(861L);
        wishlist.setUser(user);
        wishlist.setInventories(new java.util.ArrayList<>());
        Product product = new Product();
        product.setProductId(862L);
        product.setApproved(true);
        SKU sku = new SKU();
        sku.setSkuId(863L);
        sku.setProduct(product);
        sku.setActive(true);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(864L);
        inventory.setSku(sku);
        inventory.setStockAvailable(5);
        inventory.setSellingPrice(12.50);
        when(authenticationHelper.getCurrentUserId()).thenReturn("shopper@example.com");
        when(wishListRepository.findByUserEmail("shopper@example.com")).thenReturn(Optional.of(wishlist));
        when(inventoryRepository.findById(864L)).thenReturn(Optional.of(inventory));
        when(cartRepository.findByUserEmail("shopper@example.com")).thenReturn(new Cart());

        buyerService.addProductToWishlist(864L);
        buyerService.addProductToWishlist(864L);
        assertEquals(1, wishlist.getInventories().size());

        buyerService.removeWishlistItem(863L);
        assertTrue(wishlist.getInventories().isEmpty());

        buyerService.addProductToWishlist(864L);
        buyerService.moveWishlistItemToCart(863L, 1);
        assertTrue(wishlist.getInventories().isEmpty());
    }

    @Test
    void checkoutQuote_shouldCalculateTotalsAndCouponPreview() {
        Inventory first = new Inventory();
        first.setInventoryId(901L);
        first.setSellingPrice(20.00);
        first.setStockAvailable(5);
        SKU firstSku = new SKU();
        firstSku.setSkuId(901L);
        firstSku.setActive(true);
        firstSku.setProduct(new Product());
        first.setSku(firstSku);
        Product product = new Product();
        product.setApproved(true);
        firstSku.setProduct(product);

        Inventory second = new Inventory();
        second.setInventoryId(902L);
        second.setSellingPrice(30.00);
        second.setStockAvailable(4);
        SKU secondSku = new SKU();
        secondSku.setSkuId(902L);
        secondSku.setActive(true);
        secondSku.setProduct(product);
        second.setSku(secondSku);

        CheckoutQuoteRequestDTO request = new CheckoutQuoteRequestDTO();
        request.setItems(List.of(
                new CartItemRequestDTO() {{ setInventoryId(901L); setQuantity(1); }},
                new CartItemRequestDTO() {{ setInventoryId(902L); setQuantity(1); }}
        ));
        request.setShippingMethod("STANDARD");
        request.setCouponCode("SAVE10");

        when(inventoryRepository.findById(901L)).thenReturn(Optional.of(first));
        when(inventoryRepository.findById(902L)).thenReturn(Optional.of(second));

        CheckoutQuoteDTO quote = buyerService.createCheckoutQuote(request);

        assertEquals(50.00, quote.getSubtotal());
        assertTrue(quote.getDiscount() > 0.0);
        assertEquals("STANDARD", quote.getShippingMethod());
        assertNotNull(quote.getExpiresAt());
        assertTrue(quote.getTotal() > 0.0);
    }

    @Test
    void checkoutQuote_shouldRejectEmptyCartOrUnsupportedShippingMethod() {
        CheckoutQuoteRequestDTO request = new CheckoutQuoteRequestDTO();
        request.setItems(List.of());
        request.setShippingMethod("STANDARD");

        IllegalArgumentException emptyCart = assertThrows(IllegalArgumentException.class,
                () -> buyerService.createCheckoutQuote(request));
        assertTrue(emptyCart.getMessage().toLowerCase().contains("cart"));

        CheckoutQuoteRequestDTO badMethod = new CheckoutQuoteRequestDTO();
        badMethod.setItems(List.of(new CartItemRequestDTO() {{ setInventoryId(903L); setQuantity(1); }}));
        badMethod.setShippingMethod("EXPRESS_PLUS");

        IllegalArgumentException invalidMethod = assertThrows(IllegalArgumentException.class,
                () -> buyerService.createCheckoutQuote(badMethod));
        assertTrue(invalidMethod.getMessage().toLowerCase().contains("shipping"));
    }

    @Test
    void shippingMethods_shouldReturnEligibleOptionsAndRejectBlankCountry() {
        List<com.example.onlineshop.dto.ShippingMethodDTO> methods = buyerService.getShippingMethods("US", "10001", 110.00);

        assertFalse(methods.isEmpty());
        assertTrue(methods.stream().anyMatch(method -> "STANDARD".equals(method.getCode())));
        assertTrue(methods.stream().anyMatch(method -> method.isFreeShippingEligible() && method.getFee() == 0.0));

        IllegalArgumentException blankCountry = assertThrows(IllegalArgumentException.class,
                () -> buyerService.getShippingMethods(" ", "10001", 110.00));
        assertTrue(blankCountry.getMessage().toLowerCase().contains("country"));
    }

    @Test
    void couponValidation_shouldPreviewEligibleDiscountsWithoutConsuming() {
        CouponValidationRequestDTO request = new CouponValidationRequestDTO();
        request.setCouponCode("SAVE10");
        request.setSubtotal(120.00);

        CouponValidationDTO result = buyerService.validateCoupon(request);

        assertTrue(result.isValid());
        assertTrue(result.getDiscountAmount() > 0.0);
        assertEquals("SAVE10", result.getCouponCode());
        assertFalse(result.getMessage().toLowerCase().contains("expired"));
    }

    @Test
    void couponValidation_shouldRejectInvalidCodeAndLowSpend() {
        CouponValidationRequestDTO invalid = new CouponValidationRequestDTO();
        invalid.setCouponCode("NOPE");
        invalid.setSubtotal(30.00);

        CouponValidationDTO badCode = buyerService.validateCoupon(invalid);
        assertFalse(badCode.isValid());
        assertTrue(badCode.getMessage().toLowerCase().contains("invalid"));

        CouponValidationRequestDTO lowSpend = new CouponValidationRequestDTO();
        lowSpend.setCouponCode("SAVE10");
        lowSpend.setSubtotal(9.99);

        CouponValidationDTO lowSpendResult = buyerService.validateCoupon(lowSpend);
        assertFalse(lowSpendResult.isValid());
        assertTrue(lowSpendResult.getMessage().toLowerCase().contains("minimum"));
    }

    @Test
    void adminCoupons_shouldListCurrentActiveCouponsAndFilterByStatus() {
        com.example.onlineshop.model.Coupon active = new com.example.onlineshop.model.Coupon();
        active.setCouponId(1001L);
        active.setCode("SAVE10");
        active.setActive(true);
        active.setStartAt(LocalDateTime.now().minusDays(1));
        active.setEndAt(LocalDateTime.now().plusDays(10));

        when(couponRepository.findAll()).thenReturn(List.of(active));

        List<CouponDTO> result = adminService.getCouponsForAdmin("ACTIVE", null, null);

        assertEquals(1, result.size());
        assertEquals("SAVE10", result.get(0).getCode());
    }

    @Test
    void adminCoupons_shouldCreateAndPatchCouponRules() {
        CouponDTO payload = new CouponDTO();
        payload.setCode("VIP20");
        payload.setDescription("VIP seasonal discount");
        payload.setType("PERCENTAGE");
        payload.setValue(20.0);
        payload.setMinSpend(50.0);
        payload.setActive(true);

        com.example.onlineshop.model.Coupon saved = new com.example.onlineshop.model.Coupon();
        saved.setCouponId(1002L);
        saved.setCode("VIP20");
        saved.setDescription("VIP seasonal discount");
        saved.setType("PERCENTAGE");
        saved.setValue(20.0);
        saved.setMinSpend(50.0);
        saved.setActive(true);
        when(couponRepository.save(any(com.example.onlineshop.model.Coupon.class))).thenReturn(saved);

        CouponDTO created = adminService.createCouponForAdmin(payload);
        assertEquals("VIP20", created.getCode());

        CouponDTO patch = new CouponDTO();
        patch.setValue(15.0);
        patch.setMinSpend(75.0);
        when(couponRepository.findById(1002L)).thenReturn(Optional.of(saved));
        when(couponRepository.save(saved)).thenReturn(saved);

        CouponDTO updated = adminService.updateCouponForAdmin(1002L, patch);
        assertEquals(15.0, updated.getValue());
        assertEquals(75.0, updated.getMinSpend());
    }

    @Test
    void adminCoupons_shouldToggleStatusWithoutChangingHistory() {
        com.example.onlineshop.model.Coupon coupon = new com.example.onlineshop.model.Coupon();
        coupon.setCouponId(1003L);
        coupon.setCode("NEXTDAY");
        coupon.setActive(false);
        when(couponRepository.findById(1003L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(coupon)).thenReturn(coupon);

        CouponDTO result = adminService.updateCouponStatusForAdmin(1003L, "ACTIVE");

        assertTrue(result.isActive());
        assertEquals("NEXTDAY", result.getCode());
    }

    @Test
    void adminTaxRules_shouldListRegionAndEffectiveRulesWithInclusiveConfiguration() {
        LocalDateTime now = LocalDateTime.now();
        TaxRule current = new TaxRule();
        current.setRuleId(1L);
        current.setRegion("US-CA");
        current.setRate(new java.math.BigDecimal("0.0825"));
        current.setTaxInclusive(true);
        current.setEffectiveFrom(now.minusDays(10));
        current.setEffectiveTo(now.plusDays(10));
        current.setActive(true);

        TaxRule expired = new TaxRule();
        expired.setRuleId(2L);
        expired.setRegion("US-CA");
        expired.setRate(new java.math.BigDecimal("0.0750"));
        expired.setTaxInclusive(false);
        expired.setEffectiveFrom(now.minusDays(30));
        expired.setEffectiveTo(now.minusDays(11));
        expired.setActive(true);

        TaxRule otherRegion = new TaxRule();
        otherRegion.setRuleId(3L);
        otherRegion.setRegion("US-NY");
        otherRegion.setRate(new java.math.BigDecimal("0.0800"));
        otherRegion.setTaxInclusive(false);
        otherRegion.setEffectiveFrom(now.minusDays(5));
        otherRegion.setActive(true);

        when(taxRuleRepository.findAllByOrderByRegionAscEffectiveFromDesc())
                .thenReturn(List.of(current, expired, otherRegion));

        List<TaxRuleDTO> result = adminService.getTaxRulesForAdmin("us-ca", now);

        assertEquals(1, result.size());
        assertEquals("US-CA", result.get(0).getRegion());
        assertEquals(new java.math.BigDecimal("0.0825"), result.get(0).getRate());
        assertTrue(result.get(0).isTaxInclusive());
    }

    @Test
    void adminTaxRules_shouldCreateValidatedRuleAndRejectOverlappingActiveWindow() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        TaxRuleDTO request = new TaxRuleDTO();
        request.setRegion(" us-ca ");
        request.setRate(new java.math.BigDecimal("0.0825"));
        request.setTaxInclusive(true);
        request.setEffectiveFrom(start);
        request.setEffectiveTo(start.plusMonths(1));
        request.setActive(true);

        when(taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue("US-CA")).thenReturn(List.of());
        when(taxRuleRepository.save(any(TaxRule.class))).thenAnswer(invocation -> {
            TaxRule saved = invocation.getArgument(0);
            saved.setRuleId(11L);
            return saved;
        });

        TaxRuleDTO created = adminService.createTaxRuleForAdmin(request);

        assertEquals(11L, created.getRuleId());
        assertEquals("US-CA", created.getRegion());
        assertEquals(new java.math.BigDecimal("0.0825"), created.getRate());
        assertTrue(created.isTaxInclusive());

        TaxRuleDTO invalidRate = new TaxRuleDTO();
        invalidRate.setRegion("US-NY");
        invalidRate.setRate(new java.math.BigDecimal("1.2500"));
        invalidRate.setEffectiveFrom(start);
        assertThrows(IllegalArgumentException.class, () -> adminService.createTaxRuleForAdmin(invalidRate));

        TaxRule existing = new TaxRule();
        existing.setRegion("US-CA");
        existing.setEffectiveFrom(start.minusDays(1));
        existing.setEffectiveTo(start.plusDays(10));
        existing.setActive(true);
        when(taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue("US-CA")).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () -> adminService.createTaxRuleForAdmin(request));
        verify(taxRuleRepository, times(1)).save(any(TaxRule.class));
    }

    @Test
    void adminTaxRules_shouldPatchFieldsWithoutReplacingUnspecifiedValuesAndRejectConflict() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        TaxRule rule = new TaxRule();
        rule.setRuleId(21L);
        rule.setRegion("US-CA");
        rule.setRate(new java.math.BigDecimal("0.0825"));
        rule.setTaxInclusive(true);
        rule.setEffectiveFrom(start);
        rule.setEffectiveTo(start.plusMonths(1));
        rule.setActive(true);

        TaxRuleDTO patch = new TaxRuleDTO();
        patch.setRate(new java.math.BigDecimal("0.0900"));
        when(taxRuleRepository.findById(21L)).thenReturn(Optional.of(rule));
        when(taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue("US-CA")).thenReturn(List.of(rule));
        when(taxRuleRepository.save(rule)).thenReturn(rule);

        TaxRuleDTO updated = adminService.updateTaxRuleForAdmin(21L, patch);

        assertEquals(new java.math.BigDecimal("0.0900"), updated.getRate());
        assertTrue(updated.isTaxInclusive());
        assertTrue(updated.isActive());
        assertEquals(start, updated.getEffectiveFrom());

        TaxRule overlapping = new TaxRule();
        overlapping.setRuleId(22L);
        overlapping.setRegion("US-NY");
        overlapping.setEffectiveFrom(start.minusDays(1));
        overlapping.setEffectiveTo(start.plusMonths(2));
        overlapping.setActive(true);

        TaxRuleDTO conflictingPatch = new TaxRuleDTO();
        conflictingPatch.setRegion("US-NY");
        when(taxRuleRepository.findByRegionIgnoreCaseAndActiveTrue("US-NY"))
                .thenReturn(List.of(overlapping));

        assertThrows(IllegalArgumentException.class,
                () -> adminService.updateTaxRuleForAdmin(21L, conflictingPatch));
    }

    @Test
    void adminShippingZones_shouldListRegionsAndEligibilityInPriorityOrder() {
        ShippingZone first = new ShippingZone();
        first.setZoneId(1L);
        first.setName("US West");
        first.setRegionCodes(List.of("US-CA", "US-OR"));
        first.setPostalCodePrefixes(List.of("9"));
        first.setPriority(10);
        first.setActive(true);

        ShippingZone second = new ShippingZone();
        second.setZoneId(2L);
        second.setName("Canada");
        second.setRegionCodes(List.of("CA"));
        second.setPostalCodePrefixes(List.of());
        second.setPriority(20);
        second.setActive(false);

        when(shippingZoneRepository.findAllByOrderByPriorityAscZoneIdAsc()).thenReturn(List.of(first, second));

        List<ShippingZoneDTO> result = adminService.getShippingZonesForAdmin();

        assertEquals(2, result.size());
        assertEquals("US West", result.get(0).getName());
        assertEquals(List.of("US-CA", "US-OR"), result.get(0).getRegionCodes());
        assertEquals(List.of("9"), result.get(0).getPostalCodePrefixes());
        assertEquals(10, result.get(0).getPriority());
        assertFalse(result.get(1).isActive());
    }

    @Test
    void adminShippingZones_shouldRequireShippingReadPermission() throws NoSuchMethodException {
        var method = UserController.class.getMethod("getAdminShippingZones");
        var permission = method.getAnnotation(org.springframework.security.access.prepost.PreAuthorize.class);

        assertNotNull(permission);
        assertTrue(permission.value().contains("shipping:read"));
        assertTrue(permission.value().contains("ADMIN"));
    }

    @Test
    void adminShippingZones_shouldCreateZoneAndRejectAmbiguousSamePriorityMatcher() {
        ShippingZoneDTO request = new ShippingZoneDTO();
        request.setName("  California West  ");
        request.setRegionCodes(List.of(" us-ca "));
        request.setPostalCodePrefixes(List.of("94", "9"));
        request.setPriority(5);
        request.setActive(true);

        when(shippingZoneRepository.findAll()).thenReturn(List.of());
        when(shippingZoneRepository.save(any(ShippingZone.class))).thenAnswer(invocation -> {
            ShippingZone saved = invocation.getArgument(0);
            saved.setZoneId(31L);
            return saved;
        });

        ShippingZoneDTO created = adminService.createShippingZoneForAdmin(request);

        assertEquals(31L, created.getZoneId());
        assertEquals("California West", created.getName());
        assertEquals(List.of("US-CA"), created.getRegionCodes());
        assertEquals(List.of("9", "94"), created.getPostalCodePrefixes());

        ShippingZone existing = new ShippingZone();
        existing.setRegionCodes(List.of("US-CA"));
        existing.setPostalCodePrefixes(List.of("9"));
        existing.setPriority(5);
        existing.setActive(true);
        when(shippingZoneRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () -> adminService.createShippingZoneForAdmin(request));
        verify(shippingZoneRepository, times(1)).save(any(ShippingZone.class));
    }

    @Test
    void adminShippingZones_shouldPatchPartialFieldsAndRejectAmbiguousPriorityChange() {
        ShippingZone zone = new ShippingZone();
        zone.setZoneId(41L);
        zone.setName("California West");
        zone.setRegionCodes(List.of("US-CA"));
        zone.setPostalCodePrefixes(List.of("9"));
        zone.setPriority(10);
        zone.setActive(true);

        ShippingZoneDTO patch = new ShippingZoneDTO();
        patch.setName("California and Oregon");
        when(shippingZoneRepository.findById(41L)).thenReturn(Optional.of(zone));
        when(shippingZoneRepository.findAll()).thenReturn(List.of(zone));
        when(shippingZoneRepository.save(zone)).thenReturn(zone);

        ShippingZoneDTO updated = adminService.updateShippingZoneForAdmin(41L, patch);

        assertEquals("California and Oregon", updated.getName());
        assertEquals(List.of("US-CA"), updated.getRegionCodes());
        assertEquals(10, updated.getPriority());
        assertTrue(updated.isActive());

        ShippingZone overlapping = new ShippingZone();
        overlapping.setZoneId(42L);
        overlapping.setRegionCodes(List.of("US-CA"));
        overlapping.setPostalCodePrefixes(List.of("94"));
        overlapping.setPriority(5);
        overlapping.setActive(true);
        ShippingZoneDTO conflictingPatch = new ShippingZoneDTO();
        conflictingPatch.setPriority(5);
        when(shippingZoneRepository.findAll()).thenReturn(List.of(zone, overlapping));

        assertThrows(IllegalArgumentException.class,
                () -> adminService.updateShippingZoneForAdmin(41L, conflictingPatch));
    }

    @Test
    void adminShippingMethods_shouldCreateFlatRateMethodAndRejectDuplicateCode() {
        AdminShippingMethodDTO request = new AdminShippingMethodDTO();
        request.setCode("  PRIORITY ");
        request.setLabel("Priority delivery");
        request.setFee(new java.math.BigDecimal("8.50"));
        request.setFreeShippingThreshold(new java.math.BigDecimal("100.00"));
        request.setEstimate("1-2 business days");
        request.setActive(true);

        when(shippingMethodRepository.existsByCodeIgnoreCase("PRIORITY")).thenReturn(false);
        when(shippingMethodRepository.save(any(ShippingMethod.class))).thenAnswer(invocation -> {
            ShippingMethod saved = invocation.getArgument(0);
            saved.setMethodId(51L);
            return saved;
        });

        AdminShippingMethodDTO created = adminService.createShippingMethodForAdmin(request);

        assertEquals(51L, created.getMethodId());
        assertEquals("PRIORITY", created.getCode());
        assertEquals(new java.math.BigDecimal("8.50"), created.getFee());
        assertEquals(new java.math.BigDecimal("100.00"), created.getFreeShippingThreshold());
        assertEquals("1-2 business days", created.getEstimate());

        when(shippingMethodRepository.existsByCodeIgnoreCase("PRIORITY")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> adminService.createShippingMethodForAdmin(request));
        verify(shippingMethodRepository, times(1)).save(any(ShippingMethod.class));
    }

    @Test
    void adminShippingMethods_shouldRequireShippingCreatePermission() throws NoSuchMethodException {
        var method = UserController.class.getMethod("createAdminShippingMethod", AdminShippingMethodDTO.class);
        var permission = method.getAnnotation(org.springframework.security.access.prepost.PreAuthorize.class);

        assertNotNull(permission);
        assertTrue(permission.value().contains("shipping:create"));
        assertTrue(permission.value().contains("ADMIN"));
    }

    @Test
    void adminShippingMethods_shouldPatchEligibilityAndFeesWithoutReplacingUnspecifiedFields() {
        ShippingMethod method = new ShippingMethod();
        method.setMethodId(61L);
        method.setCode("PRIORITY");
        method.setLabel("Priority delivery");
        method.setFee(new java.math.BigDecimal("8.50"));
        method.setFreeShippingThreshold(new java.math.BigDecimal("100.00"));
        method.setEstimate("1-2 business days");
        method.setActive(true);

        AdminShippingMethodDTO patch = new AdminShippingMethodDTO();
        patch.setFee(new java.math.BigDecimal("9.25"));
        when(shippingMethodRepository.findById(61L)).thenReturn(Optional.of(method));
        when(shippingMethodRepository.save(method)).thenReturn(method);

        AdminShippingMethodDTO updated = adminService.updateShippingMethodForAdmin(61L, patch);

        assertEquals(new java.math.BigDecimal("9.25"), updated.getFee());
        assertEquals("Priority delivery", updated.getLabel());
        assertEquals(new java.math.BigDecimal("100.00"), updated.getFreeShippingThreshold());
        assertEquals("1-2 business days", updated.getEstimate());
        assertTrue(updated.isActive());

        AdminShippingMethodDTO invalidPatch = new AdminShippingMethodDTO();
        invalidPatch.setFee(new java.math.BigDecimal("-1.00"));
        assertThrows(IllegalArgumentException.class,
                () -> adminService.updateShippingMethodForAdmin(61L, invalidPatch));

        AdminShippingMethodDTO duplicateCodePatch = new AdminShippingMethodDTO();
        duplicateCodePatch.setCode("EXPRESS");
        when(shippingMethodRepository.existsByCodeIgnoreCaseAndMethodIdNot("EXPRESS", 61L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> adminService.updateShippingMethodForAdmin(61L, duplicateCodePatch));
        verify(shippingMethodRepository, times(1)).save(method);
    }

    @Test
    void adminShippingMethods_shouldRequireShippingUpdatePermission() throws NoSuchMethodException {
        var method = UserController.class.getMethod("patchAdminShippingMethod", Long.class, AdminShippingMethodDTO.class);
        var permission = method.getAnnotation(org.springframework.security.access.prepost.PreAuthorize.class);

        assertNotNull(permission);
        assertTrue(permission.value().contains("shipping:update"));
        assertTrue(permission.value().contains("ADMIN"));
    }

    @Test
    void customerDeletionRequest_shouldFlagUserForRetentionWindow() {
        User user = new User();
        user.setEmail("customer@example.com");
        user.setActive(true);
        user.setEmailVerified(true);

        when(authenticationHelper.getCurrentUserId()).thenReturn("customer@example.com");
        when(userRepository.findById("customer@example.com")).thenReturn(Optional.of(user));

        String result = buyerService.requestAccountDeletion();

        assertTrue(user.isDeletionRequested());
        assertNotNull(user.getDeletionRequestedAt());
        assertNotNull(user.getRetentionDeadline());
        assertTrue(result.toLowerCase().contains("retention"));
        verify(userRepository).save(user);
    }
}
