package com.example.onlineshop;

import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.user.dto.UserRolesUpdateDTO;
import com.example.onlineshop.user.dto.RoleDTO;
import com.example.onlineshop.user.dto.RolePermissionsUpdateDTO;
import com.example.onlineshop.cart.dto.CartDTO;
import com.example.onlineshop.cart.dto.CartItemRequestDTO;
import com.example.onlineshop.order.dto.AddressDTO;
import com.example.onlineshop.product.dto.BrandDTO;
import com.example.onlineshop.promotion.dto.CouponDTO;
import com.example.onlineshop.shipping.dto.TaxRuleDTO;
import com.example.onlineshop.shipping.dto.ShippingZoneDTO;
import com.example.onlineshop.shipping.dto.AdminShippingMethodDTO;
import com.example.onlineshop.product.dto.InventoryDTO;
import com.example.onlineshop.product.dto.AdminInventoryDTO;
import com.example.onlineshop.product.dto.InventoryMovementDTO;
import com.example.onlineshop.product.dto.CategoryDTO;
import com.example.onlineshop.order.dto.CheckoutQuoteDTO;
import com.example.onlineshop.order.dto.CheckoutQuoteRequestDTO;
import com.example.onlineshop.order.dto.CouponValidationDTO;
import com.example.onlineshop.order.dto.CouponValidationRequestDTO;
import com.example.onlineshop.order.dto.OrderRequestDTO;
import com.example.onlineshop.order.dto.OrderDTO;
import com.example.onlineshop.order.dto.OrderReceiptDTO;
import com.example.onlineshop.order.dto.ReorderResultDTO;
import com.example.onlineshop.order.dto.MockPaymentCompletionDTO;
import com.example.onlineshop.order.dto.MockPaymentCompletionRequest;
import com.example.onlineshop.order.dto.GuestOrderLookupRequest;
import com.example.onlineshop.order.dto.GuestOrderLookupVerifyRequest;
import com.example.onlineshop.order.dto.GuestOrderClaimRequest;
import com.example.onlineshop.content.dto.BannerDTO;
import com.example.onlineshop.content.dto.BannerUpdateDTO;
import com.example.onlineshop.content.dto.FeaturedCollectionDTO;
import com.example.onlineshop.content.dto.FeaturedCollectionUpdateDTO;
import com.example.onlineshop.product.dto.ProductDTO;
import com.example.onlineshop.product.dto.CategoryAttributeDTO;
import com.example.onlineshop.product.dto.ImageUpdateDTO;
import com.example.onlineshop.product.dto.VariantCreateDTO;
import com.example.onlineshop.product.dto.VariantUpdateDTO;
import com.example.onlineshop.product.dto.ReviewCreateDTO;
import com.example.onlineshop.product.dto.ReviewUpdateDTO;
import com.example.onlineshop.product.dto.MyReviewDTO;
import com.example.onlineshop.product.dto.AdminReviewDTO;
import com.example.onlineshop.notification.entity.Notification;
import com.example.onlineshop.notification.dto.NotificationDTO;
import com.example.onlineshop.product.dto.SKUDTO;
import com.example.onlineshop.order.entity.Address;
import com.example.onlineshop.product.entity.CategoryAttribute;
import com.example.onlineshop.product.entity.Brand;
import com.example.onlineshop.product.entity.Category;
import com.example.onlineshop.cart.entity.Cart;
import com.example.onlineshop.cart.entity.CartItem;
import com.example.onlineshop.content.entity.Banner;
import com.example.onlineshop.content.entity.FeaturedCollection;
import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.OrderItem;
import com.example.onlineshop.order.entity.ReturnRequest;
import com.example.onlineshop.order.entity.ReturnRequestItem;
import com.example.onlineshop.order.dto.ReturnItemRequestDTO;
import com.example.onlineshop.order.dto.ReturnRequestCreateDTO;
import com.example.onlineshop.product.entity.InventoryMovement;
import com.example.onlineshop.product.entity.Image;
import com.example.onlineshop.product.entity.Product;
import com.example.onlineshop.product.entity.Review;
import com.example.onlineshop.user.entity.Role;
import com.example.onlineshop.user.entity.Permission;
import com.example.onlineshop.user.entity.RolePermissionAudit;
import com.example.onlineshop.product.entity.SKU;
import com.example.onlineshop.user.entity.TokenSession;
import com.example.onlineshop.shipping.entity.TaxRule;
import com.example.onlineshop.shipping.entity.ShippingZone;
import com.example.onlineshop.shipping.entity.ShippingMethod;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.user.service.RoleService;
import com.example.onlineshop.config.controller.AdminSettingsController;
import com.example.onlineshop.config.dto.AdminSettingsDTO;
import com.example.onlineshop.config.dto.AdminSettingsUpdateDTO;
import com.example.onlineshop.config.entity.StoreSettings;
import com.example.onlineshop.config.entity.StoreSettingsAudit;
import com.example.onlineshop.config.repository.StoreSettingsRepository;
import com.example.onlineshop.config.repository.StoreSettingsAuditRepository;
import com.example.onlineshop.config.service.StoreSettingsService;
import com.example.onlineshop.admin.service.AdminDashboardService;
import com.example.onlineshop.admin.dto.AdminDashboardDTO;
import com.example.onlineshop.report.service.ReportService;
import com.example.onlineshop.order.service.UnpaidOrderExpiryService;
import com.example.onlineshop.order.service.TransactionalOutboxService;
import com.example.onlineshop.order.entity.OutboxEvent;
import com.example.onlineshop.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.onlineshop.order.dto.OrderEvent;
import com.example.onlineshop.audit.service.AdminAuditService;
import com.example.onlineshop.audit.dto.AdminAuditDTO;
import com.example.onlineshop.wishlist.entity.WishList;
import com.example.onlineshop.order.repository.AddressRepository;
import com.example.onlineshop.cart.repository.CartRepository;
import com.example.onlineshop.cart.repository.CartItemRepository;
import com.example.onlineshop.product.repository.BrandRepository;
import com.example.onlineshop.content.repository.BannerRepository;
import com.example.onlineshop.content.repository.FeaturedCollectionRepository;
import com.example.onlineshop.product.repository.CategoryRepository;
import com.example.onlineshop.product.repository.CategoryAttributeRepository;
import com.example.onlineshop.product.repository.ProductRepository;
import com.example.onlineshop.product.repository.SKURepository;
import com.example.onlineshop.product.repository.InventoryRepository;
import com.example.onlineshop.product.repository.InventoryMovementRepository;
import com.example.onlineshop.product.repository.ImageRepository;
import com.example.onlineshop.product.repository.ReviewRepository;
import com.example.onlineshop.user.repository.RoleRepository;
import com.example.onlineshop.user.repository.RolePermissionAuditRepository;
import com.example.onlineshop.user.repository.PermissionRepository;
import com.example.onlineshop.user.repository.TokenSessionRepository;
import com.example.onlineshop.user.repository.UserRepository;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.repository.ReturnRequestRepository;
import com.example.onlineshop.notification.repository.NotificationRepository;
import com.example.onlineshop.contact.dto.ContactRequestDTO;
import com.example.onlineshop.contact.dto.ContactResponseDTO;
import com.example.onlineshop.contact.entity.ContactSubmission;
import com.example.onlineshop.contact.repository.ContactSubmissionRepository;
import com.example.onlineshop.contact.service.ContactService;
import com.example.onlineshop.content.dto.StaticPageDTO;
import com.example.onlineshop.content.entity.StaticPage;
import com.example.onlineshop.content.repository.StaticPageRepository;
import com.example.onlineshop.content.service.PublicPageService;
import com.example.onlineshop.content.service.AdminPageService;
import com.example.onlineshop.email.dto.EmailTemplateDTO;
import com.example.onlineshop.email.entity.EmailTemplate;
import com.example.onlineshop.email.repository.EmailTemplateRepository;
import com.example.onlineshop.email.service.AdminEmailTemplateService;
import com.example.onlineshop.email.delivery.dto.EmailDeliveryDTO;
import com.example.onlineshop.email.delivery.entity.EmailDelivery;
import com.example.onlineshop.email.delivery.repository.EmailDeliveryRepository;
import com.example.onlineshop.email.delivery.service.AdminEmailDeliveryService;
import com.example.onlineshop.email.dto.EmailTemplateUpdateDTO;
import com.example.onlineshop.content.dto.AdminPageCreateDTO;
import com.example.onlineshop.content.dto.AdminPageUpdateDTO;
import com.example.onlineshop.support.dto.TicketDTO;
import com.example.onlineshop.support.dto.TicketCreateDTO;
import com.example.onlineshop.support.dto.TicketDetailDTO;
import com.example.onlineshop.support.dto.TicketMessageCreateDTO;
import com.example.onlineshop.support.dto.TicketMessageDTO;
import com.example.onlineshop.support.dto.AdminTicketDTO;
import com.example.onlineshop.support.dto.AdminTicketUpdateDTO;
import com.example.onlineshop.support.dto.AdminTicketMessageCreateDTO;
import com.example.onlineshop.support.entity.Ticket;
import com.example.onlineshop.support.entity.TicketMessage;
import com.example.onlineshop.support.repository.TicketRepository;
import com.example.onlineshop.support.repository.TicketMessageRepository;
import com.example.onlineshop.support.service.TicketService;
import com.example.onlineshop.support.service.AdminTicketService;
import com.example.onlineshop.support.dto.GuestTicketVerificationRequest;
import com.example.onlineshop.support.dto.GuestTicketVerificationResponse;
import com.example.onlineshop.support.service.GuestTicketVerificationService;
import com.example.onlineshop.support.dto.GuestTicketVerificationVerifyRequest;
import com.example.onlineshop.support.dto.GuestTicketAccessDTO;
import com.example.onlineshop.security.JwtUtil;
import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.common.service.BuyerService;
import com.example.onlineshop.user.service.EmailService;
import com.example.onlineshop.notification.service.NotificationDeliveryService;
import com.example.onlineshop.product.service.ImageService;
import com.example.onlineshop.order.service.AsyncOrderService;
import com.example.onlineshop.order.service.OrderProducerService;
import com.example.onlineshop.user.service.UserService;
import com.example.onlineshop.user.controller.UserController;
import com.example.onlineshop.common.security.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;
import java.util.Map;
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
    private RolePermissionAuditRepository rolePermissionAuditRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private StoreSettingsRepository storeSettingsRepository;

    @Mock
    private StoreSettingsAuditRepository storeSettingsAuditRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @InjectMocks
    private ReportService reportService;

    @InjectMocks
    private AdminAuditService adminAuditService;

    @InjectMocks
    private UnpaidOrderExpiryService unpaidOrderExpiryService;

    @Mock
    private TransactionalOutboxService outboxService;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionalOutboxService transactionalOutboxService;

    @Mock
    private TokenSessionRepository tokenSessionRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private com.example.onlineshop.wishlist.repository.WishListRepository wishListRepository;

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
    private OrderRepository orderRepository;

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @Mock
    private OrderProducerService orderProducerService;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ContactSubmissionRepository contactSubmissionRepository;

    @Mock
    private StaticPageRepository staticPageRepository;

    @Mock
    private EmailTemplateRepository emailTemplateRepository;

    @Mock
    private EmailDeliveryRepository emailDeliveryRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @InjectMocks
    private GuestTicketVerificationService guestTicketVerificationService;

    @InjectMocks
    private AdminTicketService adminTicketService;

    @Mock
    private com.example.onlineshop.promotion.repository.CouponRepository couponRepository;

    @Mock
    private com.example.onlineshop.shipping.repository.TaxRuleRepository taxRuleRepository;

    @Mock
    private com.example.onlineshop.shipping.repository.ShippingZoneRepository shippingZoneRepository;

    @Mock
    private com.example.onlineshop.shipping.repository.ShippingMethodRepository shippingMethodRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private NotificationDeliveryService notificationDeliveryService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private RoleService roleService;

    @InjectMocks
    private StoreSettingsService storeSettingsService;

    @InjectMocks
    private BuyerService buyerService;

    @InjectMocks
    private AdminService adminService;

    @InjectMocks
    private ContactService contactService;

    @InjectMocks
    private PublicPageService publicPageService;

    @InjectMocks
    private AdminPageService adminPageService;

    @InjectMocks
    private AdminEmailTemplateService adminEmailTemplateService;

    @InjectMocks
    private AdminEmailDeliveryService adminEmailDeliveryService;

    @InjectMocks
    private TicketService ticketService;

    @InjectMocks
    private ImageService imageService;

    @InjectMocks
    private AsyncOrderService asyncOrderService;

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
        List<com.example.onlineshop.order.dto.ShippingMethodDTO> methods = buyerService.getShippingMethods("US", "10001", 110.00);

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
    void orderPlacement_shouldReserveStockCreatePaymentIntentAndReplayIdempotently() {
        User user = new User();
        user.setEmail("buyer@example.com");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-ORDER-1");
        Inventory inventory = new Inventory();
        inventory.setInventoryId(990L);
        inventory.setSku(sku);
        inventory.setSellingPrice(10.0);
        inventory.setStockAvailable(5);
        inventory.setReservedQuantity(0);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(itemRequest(990L, 2)));
        request.setShippingMethod("STANDARD");
        request.setQuotedTotal(26.60);

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findByUser_emailAndIdempotencyKey("buyer@example.com", "checkout-990"))
                .thenReturn(Optional.empty(), Optional.of(savedOrder(700L)));
        when(userRepository.findById("buyer@example.com")).thenReturn(Optional.of(user));
        when(inventoryRepository.findAllById(List.of(990L))).thenReturn(List.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            if (saved.getOrderId() == null) {
                saved.setOrderId(700L);
            }
            return saved;
        });
        when(orderRepository.findById(700L)).thenAnswer(invocation -> Optional.of(savedOrder(700L)));

        var first = asyncOrderService.place(request, "checkout-990");

        assertEquals(700L, first.get("orderId"));
        assertEquals("PAID", first.get("status"));
        assertEquals(3, inventory.getStockAvailable());
        assertEquals(2, inventory.getReservedQuantity());
        verify(inventoryRepository, times(1)).save(inventory);

        var replay = asyncOrderService.place(request, "checkout-990");
        assertEquals(700L, replay.get("orderId"));
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void orderPlacement_shouldRejectStaleQuoteBeforeReservingStock() {
        User user = new User();
        user.setEmail("buyer@example.com");
        Inventory inventory = new Inventory();
        inventory.setInventoryId(991L);
        inventory.setSellingPrice(10.0);
        inventory.setStockAvailable(5);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(itemRequest(991L, 1)));
        request.setQuotedTotal(999.0);

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findByUser_emailAndIdempotencyKey("buyer@example.com", "stale-991"))
                .thenReturn(Optional.empty());
        when(userRepository.findById("buyer@example.com")).thenReturn(Optional.of(user));
        when(inventoryRepository.findAllById(List.of(991L))).thenReturn(List.of(inventory));

        assertThrows(IllegalArgumentException.class,
                () -> asyncOrderService.place(request, "stale-991"));
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void mockPayment_shouldApproveValidIntentAndBeIdempotent() {
        Order order = savedOrder(850L);
        order.setOrderStatus("FULFILLED");
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setPaymentIntentId("pi-850");
        order.setTotalPrice(26.60);
        when(orderRepository.findByPaymentIntentId("pi-850")).thenReturn(Optional.of(order));

        MockPaymentCompletionRequest request = new MockPaymentCompletionRequest();
        request.setPaymentIntentId("pi-850");
        request.setOutcome("approved");
        request.setAmount(26.60);

        MockPaymentCompletionDTO result = asyncOrderService.completeMockPayment(request, "local-mock-payment-token");

        assertEquals("PAID", result.getPaymentStatus());
        assertEquals("PAID", result.getOrderStatus());
        assertEquals("PAID", order.getPaymentStatus());
        verify(orderRepository).save(order);

        MockPaymentCompletionDTO replay = asyncOrderService.completeMockPayment(request, "local-mock-payment-token");
        assertEquals("PAID", replay.getPaymentStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void mockPayment_shouldRejectInvalidTokenAndAmountBeforeMutatingOrder() {
        Order order = savedOrder(851L);
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setPaymentIntentId("pi-851");
        order.setTotalPrice(40.00);
        when(orderRepository.findByPaymentIntentId("pi-851")).thenReturn(Optional.of(order));

        MockPaymentCompletionRequest request = new MockPaymentCompletionRequest();
        request.setPaymentIntentId("pi-851");
        request.setOutcome("declined");
        request.setAmount(39.00);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> asyncOrderService.completeMockPayment(request, "wrong-token"));
        assertThrows(IllegalArgumentException.class,
                () -> asyncOrderService.completeMockPayment(request, "local-mock-payment-token"));
        assertEquals("REQUIRES_PAYMENT", order.getPaymentStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void mockPayment_shouldRejectUnsupportedOutcome() {
        Order order = savedOrder(852L);
        order.setPaymentIntentId("pi-852");
        order.setTotalPrice(10.00);
        when(orderRepository.findByPaymentIntentId("pi-852")).thenReturn(Optional.of(order));

        MockPaymentCompletionRequest request = new MockPaymentCompletionRequest();
        request.setPaymentIntentId("pi-852");
        request.setOutcome("pending");
        request.setAmount(10.00);

        assertThrows(IllegalArgumentException.class,
                () -> asyncOrderService.completeMockPayment(request, "local-mock-payment-token"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void guestOrderLookup_shouldSendChallengeForMatchWithoutRevealingExistence() {
        Order order = savedOrder(860L);
        order.setGuestOrderReference("GUEST-860");
        order.setGuestEmail("guest@example.com");
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-860", "guest@example.com"))
                .thenReturn(Optional.of(order));

        GuestOrderLookupRequest request = new GuestOrderLookupRequest();
        request.setOrderReference(" GUEST-860 ");
        request.setEmail("Guest@Example.com");

        var response = buyerService.requestGuestOrderLookup(request);

        assertTrue(response.getMessage().contains("If an eligible guest order exists"));
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), anyString());
    }

    @Test
    void guestOrderLookup_shouldReturnSameGenericResponseForMissingOrder() {
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("MISSING", "guest@example.com"))
                .thenReturn(Optional.empty());

        GuestOrderLookupRequest request = new GuestOrderLookupRequest();
        request.setOrderReference("MISSING");
        request.setEmail("guest@example.com");

        var response = buyerService.requestGuestOrderLookup(request);

        assertEquals("If an eligible guest order exists, verification instructions have been sent.", response.getMessage());
        verify(emailService, never()).sendGuestOrderLookupEmail(anyString(), anyString());
    }

    @Test
    void guestOrderLookup_shouldRejectMalformedRequestBeforeLookup() {
        GuestOrderLookupRequest request = new GuestOrderLookupRequest();
        request.setOrderReference("GUEST-860");
        request.setEmail("not-an-email");

        assertThrows(IllegalArgumentException.class, () -> buyerService.requestGuestOrderLookup(request));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void guestOrderLookupVerify_shouldConsumeChallengeAndIssueScopedAccess() {
        Order order = savedOrder(861L);
        order.setGuestOrderReference("GUEST-861");
        order.setGuestEmail("guest@example.com");
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-861", "guest@example.com"))
                .thenReturn(Optional.of(order));

        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-861");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);

        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        GuestOrderLookupVerifyRequest verifyRequest = new GuestOrderLookupVerifyRequest();
        verifyRequest.setChallenge(emailCaptor.getValue());

        var access = buyerService.verifyGuestOrderLookup(verifyRequest);

        assertEquals(861L, access.getOrderId());
        assertNotNull(access.getAccessToken());
        assertTrue(access.getExpiresAt().isAfter(java.time.LocalDateTime.now()));
        assertTrue(buyerService.hasGuestOrderAccess(861L, access.getAccessToken()));
        assertFalse(buyerService.hasGuestOrderAccess(862L, access.getAccessToken()));
    }

    @Test
    void guestOrderLookupVerify_shouldRejectReplayAndInvalidChallenge() {
        GuestOrderLookupVerifyRequest request = new GuestOrderLookupVerifyRequest();
        request.setChallenge("missing-challenge");
        assertThrows(IllegalArgumentException.class, () -> buyerService.verifyGuestOrderLookup(request));

        Order order = savedOrder(862L);
        order.setGuestOrderReference("GUEST-862");
        order.setGuestEmail("guest@example.com");
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-862", "guest@example.com"))
                .thenReturn(Optional.of(order));
        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-862");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);
        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        request.setChallenge(emailCaptor.getValue());
        buyerService.verifyGuestOrderLookup(request);

        assertThrows(IllegalArgumentException.class, () -> buyerService.verifyGuestOrderLookup(request));
    }

    @Test
    void guestOrder_shouldRequireScopedCredentialAndReturnOnlyVerifiedGuestOrder() {
        Order order = savedOrder(863L);
        order.setOrderStatus("PAID");
        order.setGuestOrderReference("GUEST-863");
        order.setGuestEmail("guest@example.com");
        order.setOrderItems(List.of());
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-863", "guest@example.com"))
                .thenReturn(Optional.of(order));
        when(orderRepository.findById(863L)).thenReturn(Optional.of(order));

        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-863");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);
        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        GuestOrderLookupVerifyRequest verifyRequest = new GuestOrderLookupVerifyRequest();
        verifyRequest.setChallenge(emailCaptor.getValue());
        var access = buyerService.verifyGuestOrderLookup(verifyRequest);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.getVerifiedGuestOrder(863L, null));
        var result = buyerService.getVerifiedGuestOrder(863L, access.getAccessToken());
        assertEquals(863L, result.getOrderId());
        assertEquals("PAID", result.getOrderStatus());
    }

    @Test
    void guestOrder_shouldRejectCredentialScopedToAnotherOrder() {
        Order order = savedOrder(864L);
        order.setGuestOrderReference("GUEST-864");
        order.setGuestEmail("guest@example.com");
        order.setOrderItems(List.of());
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-864", "guest@example.com"))
                .thenReturn(Optional.of(order));
        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-864");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);
        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        GuestOrderLookupVerifyRequest verifyRequest = new GuestOrderLookupVerifyRequest();
        verifyRequest.setChallenge(emailCaptor.getValue());
        var access = buyerService.verifyGuestOrderLookup(verifyRequest);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.getVerifiedGuestOrder(999L, access.getAccessToken()));
    }

    @Test
    void guestOrderClaim_shouldAssociateVerifiedOrderAndRecordAuditHistory() {
        User user = new User();
        user.setEmail("guest@example.com");
        Order order = savedOrder(865L);
        order.setOrderStatus("PAID");
        order.setUser(null);
        order.setGuestOrderReference("GUEST-865");
        order.setGuestEmail("guest@example.com");
        order.setOrderItems(List.of());
        when(authenticationHelper.getCurrentUserId()).thenReturn("guest@example.com");
        when(userRepository.findById("guest@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-865", "guest@example.com"))
                .thenReturn(Optional.of(order));
        when(orderRepository.findById(865L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-865");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);
        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        GuestOrderLookupVerifyRequest verifyRequest = new GuestOrderLookupVerifyRequest();
        verifyRequest.setChallenge(emailCaptor.getValue());
        var access = buyerService.verifyGuestOrderLookup(verifyRequest);

        GuestOrderClaimRequest claim = new GuestOrderClaimRequest();
        claim.setAccessToken(access.getAccessToken());
        var result = buyerService.claimGuestOrder(claim);

        assertEquals("guest@example.com", order.getUser().getEmail());
        assertEquals("CLAIMED", order.getStatusHistory().get(order.getStatusHistory().size() - 1).getStatus());
        assertEquals(865L, result.getOrderId());
        assertFalse(buyerService.hasGuestOrderAccess(865L, access.getAccessToken()));
        verify(orderRepository).save(order);
    }

    @Test
    void guestOrderClaim_shouldRejectCredentialForDifferentAuthenticatedEmail() {
        Order order = savedOrder(866L);
        order.setUser(null);
        order.setGuestOrderReference("GUEST-866");
        order.setGuestEmail("guest@example.com");
        when(authenticationHelper.getCurrentUserId()).thenReturn("other@example.com");
        when(orderRepository.findByGuestOrderReferenceAndGuestEmailIgnoreCase("GUEST-866", "guest@example.com"))
                .thenReturn(Optional.of(order));
        GuestOrderLookupRequest lookup = new GuestOrderLookupRequest();
        lookup.setOrderReference("GUEST-866");
        lookup.setEmail("guest@example.com");
        buyerService.requestGuestOrderLookup(lookup);
        var emailCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestOrderLookupEmail(eq("guest@example.com"), emailCaptor.capture());
        GuestOrderLookupVerifyRequest verifyRequest = new GuestOrderLookupVerifyRequest();
        verifyRequest.setChallenge(emailCaptor.getValue());
        var access = buyerService.verifyGuestOrderLookup(verifyRequest);

        GuestOrderClaimRequest claim = new GuestOrderClaimRequest();
        claim.setAccessToken(access.getAccessToken());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.claimGuestOrder(claim));
        assertNull(order.getUser());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void adminOrders_shouldFilterAndPageOrdersForStaffList() {
        User customer = new User();
        customer.setEmail("buyer@example.com");
        Order paid = savedOrder(870L);
        paid.setOrderDate(java.time.LocalDate.of(2026, 10, 20));
        paid.setOrderStatus("PAID");
        paid.setPaymentStatus("PAID");
        paid.setUser(customer);
        Order pending = savedOrder(871L);
        pending.setOrderDate(java.time.LocalDate.of(2026, 10, 21));
        pending.setOrderStatus("PENDING");
        pending.setPaymentStatus("REQUIRES_PAYMENT");
        pending.setUser(customer);
        when(orderRepository.findAll()).thenReturn(List.of(paid, pending));

        var result = adminService.getAdminOrders(0, 10, "paid", "paid", "buyer@example.com",
                java.time.LocalDate.of(2026, 10, 1), java.time.LocalDate.of(2026, 10, 31));

        assertEquals(1, result.getTotalElements());
        assertEquals(870L, result.getContent().get(0).getOrderId());
    }

    @Test
    void adminOrders_shouldRejectInvalidPageSize() {
        assertThrows(IllegalArgumentException.class,
                () -> adminService.getAdminOrders(0, 101, null, null, null, null, null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void adminOrderDetail_shouldExposeFinancialPaymentShipmentAndHistorySnapshots() {
        Order order = savedOrder(872L);
        order.setSubtotal(100.00);
        order.setDiscount(10.00);
        order.setShippingFee(5.00);
        order.setTax(7.60);
        order.setShippingMethod("STANDARD");
        order.setPaymentIntentId("pi-admin-872");
        order.setPaymentStatus("PAID");
        order.setStatusHistory(List.of(new com.example.onlineshop.order.entity.OrderStatusHistory(
                order, "PAID", "Payment intent completed")));
        when(orderRepository.findById(872L)).thenReturn(Optional.of(order));

        OrderDTO result = adminService.getAdminOrder(872L);

        assertEquals(100.00, result.getSubtotal());
        assertEquals(10.00, result.getDiscount());
        assertEquals(5.00, result.getShippingFee());
        assertEquals(7.60, result.getTax());
        assertEquals("STANDARD", result.getShippingMethod());
        assertEquals("pi-admin-872", result.getPaymentIntentId());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(1, result.getPaymentAttempts().size());
    }

    @Test
    void ownReturns_shouldListOnlyReturnsForAuthenticatedCustomersOrder() {
        User customer = new User();
        customer.setEmail("buyer@example.com");
        Order order = savedOrder(885L);
        order.setUser(customer);
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(901L);
        request.setOrder(order);
        request.setStatus("REQUESTED");
        request.setReason("Damaged item");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(885L)).thenReturn(Optional.of(order));
        when(returnRequestRepository.findByOrder_OrderIdAndOrder_User_EmailIgnoreCaseOrderByRequestedAtDesc(885L, "buyer@example.com"))
                .thenReturn(List.of(request));

        var result = buyerService.getOwnReturnRequests(885L);

        assertEquals(1, result.size());
        assertEquals(901L, result.get(0).getReturnRequestId());
        assertEquals("REQUESTED", result.get(0).getStatus());
        assertEquals("Damaged item", result.get(0).getReason());
    }

    @Test
    void ownReturns_shouldRejectAnotherCustomersOrder() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        Order order = savedOrder(886L);
        order.setUser(owner);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(886L)).thenReturn(Optional.of(order));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.getOwnReturnRequests(886L));
        verifyNoInteractions(returnRequestRepository);
    }

    @Test
    void createReturn_shouldValidateDeliveredOrderItemsAndAvoidRefundMutation() {
        User customer = new User();
        customer.setEmail("buyer@example.com");
        Order order = savedOrder(887L);
        order.setUser(customer);
        order.setOrderStatus("DELIVERED");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-RETURN-1");
        OrderItem orderItem = new OrderItem(sku, 2, 20.0);
        orderItem.setOrderItemId(601L);
        orderItem.setOrder(order);
        order.setOrderItems(List.of(orderItem));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(887L)).thenReturn(Optional.of(order));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReturnItemRequestDTO item = new ReturnItemRequestDTO();
        item.setOrderItemId(601L);
        item.setQuantity(1);
        item.setReason("Damaged on arrival");
        ReturnRequestCreateDTO request = new ReturnRequestCreateDTO();
        request.setReason("Please review");
        request.setItems(List.of(item));

        var result = buyerService.createReturnRequest(887L, request);

        assertEquals("REQUESTED", result.getStatus());
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getItems().get(0).getQuantity());
        assertEquals("Damaged on arrival", result.getItems().get(0).getReason());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void createReturn_shouldRejectUnfulfilledOrderAndExcessQuantity() {
        User customer = new User();
        customer.setEmail("buyer@example.com");
        Order order = savedOrder(888L);
        order.setUser(customer);
        order.setOrderStatus("SHIPPED");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-RETURN-2");
        OrderItem orderItem = new OrderItem(sku, 1, 20.0);
        orderItem.setOrderItemId(602L);
        order.setOrderItems(List.of(orderItem));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(888L)).thenReturn(Optional.of(order));

        ReturnItemRequestDTO item = new ReturnItemRequestDTO();
        item.setOrderItemId(602L);
        item.setQuantity(2);
        item.setReason("Changed mind");
        ReturnRequestCreateDTO request = new ReturnRequestCreateDTO();
        request.setItems(List.of(item));

        assertThrows(IllegalStateException.class, () -> buyerService.createReturnRequest(888L, request));
        order.setOrderStatus("DELIVERED");
        assertThrows(IllegalArgumentException.class, () -> buyerService.createReturnRequest(888L, request));
        verify(returnRequestRepository, never()).save(any(ReturnRequest.class));
    }

    @Test
    void adminReturns_shouldFilterByStatusAndEligibility() {
        Order delivered = savedOrder(889L);
        delivered.setOrderStatus("DELIVERED");
        ReturnRequest eligible = new ReturnRequest();
        eligible.setReturnRequestId(910L);
        eligible.setOrder(delivered);
        eligible.setStatus("REQUESTED");
        eligible.setReason("Damaged item");

        Order shipped = savedOrder(890L);
        shipped.setOrderStatus("SHIPPED");
        ReturnRequest ineligible = new ReturnRequest();
        ineligible.setReturnRequestId(911L);
        ineligible.setOrder(shipped);
        ineligible.setStatus("REQUESTED");
        ineligible.setReason("Changed mind");
        when(returnRequestRepository.findAll()).thenReturn(List.of(eligible, ineligible));

        var result = adminService.getAdminReturns("requested", true);

        assertEquals(1, result.size());
        assertEquals(910L, result.get(0).getReturnRequestId());
    }

    @Test
    void adminReturns_shouldFilterNonRequestedStatus() {
        Order delivered = savedOrder(891L);
        delivered.setOrderStatus("DELIVERED");
        ReturnRequest rejected = new ReturnRequest();
        rejected.setReturnRequestId(912L);
        rejected.setOrder(delivered);
        rejected.setStatus("REJECTED");
        rejected.setReason("Outside return window");
        when(returnRequestRepository.findAll()).thenReturn(List.of(rejected));

        var result = adminService.getAdminReturns("REJECTED", null);

        assertEquals(1, result.size());
        assertEquals("REJECTED", result.get(0).getStatus());
    }

    @Test
    void approveReturn_shouldRequireEligibilityReasonAndRecordReviewerAudit() {
        Order order = savedOrder(892L);
        order.setOrderStatus("DELIVERED");
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(913L);
        request.setOrder(order);
        request.setStatus("REQUESTED");
        request.setReason("Damaged item");
        when(returnRequestRepository.findById(913L)).thenReturn(Optional.of(request));
        when(returnRequestRepository.save(request)).thenReturn(request);
        when(authenticationHelper.getCurrentUserId()).thenReturn("staff@example.com");

        var result = adminService.approveReturn(913L, "Evidence reviewed");

        assertEquals("APPROVED", result.getStatus());
        assertEquals("Evidence reviewed", result.getDecisionReason());
        assertEquals("staff@example.com", result.getReviewedBy());
        assertNotNull(result.getReviewedAt());
        verify(returnRequestRepository).save(request);
    }

    @Test
    void approveReturn_shouldRejectMissingReasonAndIneligibleReturn() {
        Order order = savedOrder(893L);
        order.setOrderStatus("SHIPPED");
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(914L);
        request.setOrder(order);
        request.setStatus("REQUESTED");
        when(returnRequestRepository.findById(914L)).thenReturn(Optional.of(request));
        assertThrows(IllegalStateException.class, () -> adminService.approveReturn(914L, "Approved"));

        order.setOrderStatus("DELIVERED");
        assertThrows(IllegalArgumentException.class, () -> adminService.approveReturn(914L, " "));
        verify(returnRequestRepository, never()).save(any(ReturnRequest.class));
    }

    @Test
    void rejectReturn_shouldStoreCustomerVisibleReasonAndReviewerAudit() {
        Order order = savedOrder(894L);
        order.setOrderStatus("DELIVERED");
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(915L);
        request.setOrder(order);
        request.setStatus("REQUESTED");
        request.setReason("Changed mind");
        when(returnRequestRepository.findById(915L)).thenReturn(Optional.of(request));
        when(returnRequestRepository.save(request)).thenReturn(request);
        when(authenticationHelper.getCurrentUserId()).thenReturn("staff@example.com");

        var result = adminService.rejectReturn(915L, "Return window has expired");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Return window has expired", result.getDecisionReason());
        assertEquals("staff@example.com", result.getReviewedBy());
        assertNotNull(result.getReviewedAt());
        verify(returnRequestRepository).save(request);
    }

    @Test
    void rejectReturn_shouldRejectMissingReasonAndAlreadyDecidedRequest() {
        Order order = savedOrder(895L);
        order.setOrderStatus("DELIVERED");
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(916L);
        request.setOrder(order);
        request.setStatus("REQUESTED");
        when(returnRequestRepository.findById(916L)).thenReturn(Optional.of(request));
        assertThrows(IllegalArgumentException.class, () -> adminService.rejectReturn(916L, " "));
        request.setStatus("APPROVED");
        assertThrows(IllegalStateException.class, () -> adminService.rejectReturn(916L, "Too late"));
        verify(returnRequestRepository, never()).save(any(ReturnRequest.class));
    }

    @Test
    void receiveReturn_shouldRecordInspectionForApprovedReturn() {
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(917L);
        request.setOrder(savedOrder(919L));
        request.setStatus("APPROVED");
        when(returnRequestRepository.findById(917L)).thenReturn(Optional.of(request));
        when(returnRequestRepository.save(request)).thenReturn(request);
        when(authenticationHelper.getCurrentUserId()).thenReturn("warehouse@example.com");

        var result = adminService.receiveReturn(917L, "Package received; item inspected");

        assertEquals("RECEIVED", result.getStatus());
        assertEquals("Package received; item inspected", result.getInspectionNotes());
        assertEquals("warehouse@example.com", result.getReceivedBy());
        assertNotNull(result.getReceivedAt());
        verify(returnRequestRepository).save(request);
    }

    @Test
    void receiveReturn_shouldRejectUnapprovedOrMissingInspectionNotes() {
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(918L);
        request.setStatus("REQUESTED");
        when(returnRequestRepository.findById(918L)).thenReturn(Optional.of(request));
        assertThrows(IllegalStateException.class, () -> adminService.receiveReturn(918L, "Inspected"));
        request.setStatus("APPROVED");
        assertThrows(IllegalArgumentException.class, () -> adminService.receiveReturn(918L, " "));
        verify(returnRequestRepository, never()).save(any(ReturnRequest.class));
    }

    @Test
    void resolveReturn_shouldRecordRefundDecisionWithoutExecutingRefund() {
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(920L);
        request.setOrder(savedOrder(921L));
        request.setStatus("RECEIVED");
        when(returnRequestRepository.findById(920L)).thenReturn(Optional.of(request));
        when(returnRequestRepository.save(request)).thenReturn(request);
        when(authenticationHelper.getCurrentUserId()).thenReturn("staff@example.com");

        var result = adminService.resolveReturn(920L, "REFUND", 25.50, "Approved after inspection");

        assertEquals("RESOLVED", result.getStatus());
        assertEquals("REFUND", result.getRefundDecision());
        assertEquals(25.50, result.getRefundAmount());
        assertEquals("staff@example.com", result.getResolvedBy());
        assertNotNull(result.getResolvedAt());
        verify(returnRequestRepository).save(request);
        verifyNoInteractions(emailService);
    }

    @Test
    void resolveReturn_shouldRejectUnreceivedOrInvalidRefundDecision() {
        ReturnRequest request = new ReturnRequest();
        request.setReturnRequestId(922L);
        request.setOrder(savedOrder(923L));
        request.setStatus("APPROVED");
        when(returnRequestRepository.findById(922L)).thenReturn(Optional.of(request));
        assertThrows(IllegalStateException.class,
                () -> adminService.resolveReturn(922L, "REFUND", 10.0, "Reason"));
        request.setStatus("RECEIVED");
        assertThrows(IllegalArgumentException.class,
                () -> adminService.resolveReturn(922L, "NO_REFUND", 1.0, "Reason"));
        verify(returnRequestRepository, never()).save(any(ReturnRequest.class));
    }

    @Test
    void createReview_shouldRequireVerifiedPurchaserAndPreventDuplicateReviews() {
        User user = new User();
        user.setEmail("buyer@example.com");
        user.setEmailVerified(true);
        Product product = new Product();
        product.setProductId(930L);
        SKU sku = new SKU();
        sku.setProduct(product);
        Order order = savedOrder(931L);
        order.setOrderStatus("DELIVERED");
        OrderItem item = new OrderItem(sku, 1, 10.0);
        order.setOrderItems(List.of(item));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(userRepository.findById("buyer@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(930L)).thenReturn(Optional.of(product));
        when(reviewRepository.findByUserIdAndProduct_ProductId("buyer@example.com", 930L)).thenReturn(Optional.empty());
        when(orderRepository.findByUser_email("buyer@example.com")).thenReturn(List.of(order));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCreateDTO request = new ReviewCreateDTO();
        request.setReviewText("Excellent product");
        request.setRating(5);
        Review result = buyerService.createEligibleReview(930L, request);

        assertEquals("buyer@example.com", result.getUserId());
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));

        when(reviewRepository.findByUserIdAndProduct_ProductId("buyer@example.com", 930L))
                .thenReturn(Optional.of(result));
        assertThrows(IllegalStateException.class, () -> buyerService.createEligibleReview(930L, request));
    }

    @Test
    void createReview_shouldRejectUnverifiedOrNonPurchasingCustomer() {
        User user = new User();
        user.setEmail("buyer@example.com");
        user.setEmailVerified(false);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(userRepository.findById("buyer@example.com")).thenReturn(Optional.of(user));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.createEligibleReview(930L, new ReviewCreateDTO()));
    }

    @Test
    void updateReview_shouldAllowOwnerAndResetModerationStatus() {
        Review review = new Review();
        review.setId(940L);
        review.setUserId("buyer@example.com");
        review.setReviewText("Original");
        review.setRating(3);
        review.setModerationStatus("APPROVED");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findById(940L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        ReviewUpdateDTO update = new ReviewUpdateDTO();
        update.setReviewText("Updated");
        update.setRating(5);

        Review result = buyerService.updateOwnReview(940L, update);

        assertEquals("Updated", result.getReviewText());
        assertEquals(5, result.getRating());
        assertEquals("PENDING", result.getModerationStatus());
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_shouldRejectAnotherUsersReview() {
        Review review = new Review();
        review.setId(941L);
        review.setUserId("owner@example.com");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findById(941L)).thenReturn(Optional.of(review));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.updateOwnReview(941L, new ReviewUpdateDTO()));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_shouldHideOwnerReviewAndRemoveItFromPublicResults() {
        Review review = new Review();
        review.setId(942L);
        review.setUserId("buyer@example.com");
        review.setRating(5);
        review.setModerationStatus("APPROVED");
        Product product = new Product();
        product.setProductId(942L);
        review.setProduct(product);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findById(942L)).thenReturn(Optional.of(review));
        when(reviewRepository.findByProductId(942L)).thenReturn(List.of(review));
        when(productRepository.findById(942L)).thenReturn(Optional.of(product));

        buyerService.deleteOwnReview(942L);

        assertEquals("DELETED", review.getModerationStatus());
        verify(reviewRepository).save(review);
        assertTrue(buyerService.getProductReviews(942L).isEmpty());
    }

    @Test
    void deleteReview_shouldRejectAnotherUsersReview() {
        Review review = new Review();
        review.setId(943L);
        review.setUserId("owner@example.com");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findById(943L)).thenReturn(Optional.of(review));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.deleteOwnReview(943L));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void ownReviews_shouldListModerationSafeDetailsInReverseOrder() {
        Product product = new Product();
        product.setProductId(950L);
        product.setName("Running shoes");
        Review approved = new Review();
        approved.setId(952L);
        approved.setUserId("buyer@example.com");
        approved.setProduct(product);
        approved.setReviewText("Great fit");
        approved.setRating(5);
        approved.setModerationStatus("APPROVED");
        Review pending = new Review();
        pending.setId(951L);
        pending.setUserId("buyer@example.com");
        pending.setProduct(product);
        pending.setReviewText("Waiting for review");
        pending.setRating(4);
        pending.setModerationStatus("PENDING");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findByUserIdOrderByIdDesc("buyer@example.com"))
                .thenReturn(List.of(approved, pending));

        List<MyReviewDTO> result = buyerService.getOwnReviews();

        assertEquals(2, result.size());
        assertEquals(952L, result.get(0).getReviewId());
        assertEquals("Running shoes", result.get(0).getProductName());
        assertEquals("APPROVED", result.get(0).getModerationStatus());
        assertEquals("PENDING", result.get(1).getModerationStatus());
    }

    @Test
    void ownReviews_shouldReturnEmptyListWhenCustomerHasNoReviews() {
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(reviewRepository.findByUserIdOrderByIdDesc("buyer@example.com")).thenReturn(List.of());

        assertTrue(buyerService.getOwnReviews().isEmpty());
    }

    @Test
    void adminReviews_shouldListModerationHistoryAndFilterByStatus() {
        Product product = new Product();
        product.setProductId(960L);
        product.setName("Coffee maker");
        Review pending = new Review();
        pending.setId(962L);
        pending.setUserId("buyer@example.com");
        pending.setProduct(product);
        pending.setReviewText("Needs review");
        pending.setRating(3);
        pending.setModerationStatus("PENDING");
        Review approved = new Review();
        approved.setId(961L);
        approved.setUserId("other@example.com");
        approved.setProduct(product);
        approved.setReviewText("Good product");
        approved.setRating(4);
        approved.setModerationStatus("APPROVED");
        when(reviewRepository.findAllByOrderByIdDesc()).thenReturn(List.of(pending, approved));

        List<AdminReviewDTO> all = adminService.getAdminReviews(null);
        List<AdminReviewDTO> pendingOnly = adminService.getAdminReviews("pending");

        assertEquals(2, all.size());
        assertEquals(962L, all.get(0).getReviewId());
        assertEquals(1, pendingOnly.size());
        assertEquals("buyer@example.com", pendingOnly.get(0).getUserId());
    }

    @Test
    void adminReviews_shouldRejectOversizedStatusFilter() {
        assertThrows(IllegalArgumentException.class,
                () -> adminService.getAdminReviews("x".repeat(33)));
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void approveReview_shouldSetApprovedStatusAndRecalculateProductAggregate() {
        Product product = new Product();
        product.setProductId(970L);
        product.setName("Coffee maker");
        Review review = new Review();
        review.setId(971L);
        review.setUserId("buyer@example.com");
        review.setProduct(product);
        review.setReviewText("Excellent");
        review.setRating(5);
        review.setModerationStatus("PENDING");
        Review otherApproved = new Review();
        otherApproved.setId(972L);
        otherApproved.setProduct(product);
        otherApproved.setRating(3);
        otherApproved.setModerationStatus("APPROVED");
        when(reviewRepository.findById(971L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByProductId(970L)).thenReturn(List.of(review, otherApproved));

        AdminReviewDTO result = adminService.approveReview(971L);

        assertEquals("APPROVED", review.getModerationStatus());
        assertEquals("APPROVED", result.getModerationStatus());
        assertEquals(2, product.getReviewCount());
        assertEquals(4.0, product.getAverageRating());
        verify(reviewRepository).save(review);
        verify(productRepository).save(product);
    }

    @Test
    void approveReview_shouldRejectDeletedOrMissingReview() {
        Review deleted = new Review();
        deleted.setId(973L);
        deleted.setModerationStatus("DELETED");
        when(reviewRepository.findById(973L)).thenReturn(Optional.of(deleted));
        assertThrows(IllegalStateException.class, () -> adminService.approveReview(973L));

        when(reviewRepository.findById(974L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adminService.approveReview(974L));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void rejectReview_shouldStoreReasonAuditAndRemoveReviewFromAggregate() {
        Product product = new Product();
        product.setProductId(975L);
        Review review = new Review();
        review.setId(976L);
        review.setUserId("buyer@example.com");
        review.setProduct(product);
        review.setReviewText("Not useful");
        review.setRating(2);
        review.setModerationStatus("APPROVED");
        when(authenticationHelper.getCurrentUserId()).thenReturn("admin@example.com");
        when(reviewRepository.findById(976L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByProductId(975L)).thenReturn(List.of(review));

        AdminReviewDTO result = adminService.rejectReview(976L, "Violates review guidelines");

        assertEquals("REJECTED", result.getModerationStatus());
        assertEquals("Violates review guidelines", result.getModerationReason());
        assertEquals("admin@example.com", result.getModeratedBy());
        assertNotNull(result.getModeratedAt());
        assertEquals(0, product.getReviewCount());
        assertEquals(0.0, product.getAverageRating());
        verify(reviewRepository).save(review);
        verify(productRepository).save(product);
    }

    @Test
    void rejectReview_shouldRequireReasonAndRejectDeletedReview() {
        assertThrows(IllegalArgumentException.class, () -> adminService.rejectReview(977L, " "));

        Review deleted = new Review();
        deleted.setId(978L);
        deleted.setModerationStatus("DELETED");
        when(reviewRepository.findById(978L)).thenReturn(Optional.of(deleted));
        assertThrows(IllegalStateException.class, () -> adminService.rejectReview(978L, "Reason"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void hideReview_shouldAuditAndRemoveApprovedReviewFromAggregateAndPublicResults() {
        Product product = new Product();
        product.setProductId(980L);
        Review review = new Review();
        review.setId(981L);
        review.setUserId("buyer@example.com");
        review.setProduct(product);
        review.setReviewText("Inappropriate");
        review.setRating(1);
        review.setModerationStatus("APPROVED");
        when(authenticationHelper.getCurrentUserId()).thenReturn("admin@example.com");
        when(reviewRepository.findById(981L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByProductId(980L)).thenReturn(List.of(review));
        when(productRepository.findById(980L)).thenReturn(Optional.of(product));

        AdminReviewDTO result = adminService.hideReview(981L, "Violates content policy");

        assertEquals("HIDDEN", result.getModerationStatus());
        assertEquals("Violates content policy", result.getModerationReason());
        assertEquals("admin@example.com", result.getModeratedBy());
        assertNotNull(result.getModeratedAt());
        assertEquals(0, product.getReviewCount());
        verify(reviewRepository).save(review);
        verify(productRepository).save(product);

        when(reviewRepository.findByProductId(980L)).thenReturn(List.of(review));
        assertTrue(buyerService.getProductReviews(980L).isEmpty());
    }

    @Test
    void hideReview_shouldUseDefaultReasonAndRejectDeletedReview() {
        Product product = new Product();
        product.setProductId(982L);
        Review review = new Review();
        review.setId(983L);
        review.setProduct(product);
        when(reviewRepository.findById(983L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByProductId(982L)).thenReturn(List.of(review));

        AdminReviewDTO result = adminService.hideReview(983L, null);

        assertEquals("Hidden by moderation", result.getModerationReason());
        review.setModerationStatus("DELETED");
        assertThrows(IllegalStateException.class, () -> adminService.hideReview(983L, "Reason"));
    }

    @Test
    void ownNotifications_shouldReturnOnlyPagedCurrentUserNotifications() {
        Notification first = new Notification();
        first.setNotificationId(990L);
        first.setUserId("buyer@example.com");
        first.setTitle("Order shipped");
        first.setMessage("Your order is on the way");
        first.setType("ORDER");
        org.springframework.data.domain.Page<Notification> page =
                new org.springframework.data.domain.PageImpl<>(List.of(first));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(
                eq("buyer@example.com"), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        var result = buyerService.getOwnNotifications(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("Order shipped", result.getContent().get(0).getTitle());
        assertEquals("ORDER", result.getContent().get(0).getType());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(
                eq("buyer@example.com"), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void ownNotifications_shouldRejectInvalidPageParameters() {
        assertThrows(IllegalArgumentException.class, () -> buyerService.getOwnNotifications(-1, 20));
        assertThrows(IllegalArgumentException.class, () -> buyerService.getOwnNotifications(0, 0));
        assertThrows(IllegalArgumentException.class, () -> buyerService.getOwnNotifications(0, 101));
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void markOwnNotificationRead_shouldMarkUnreadNotificationAndReturnIt() {
        Notification notification = new Notification();
        notification.setNotificationId(991L);
        notification.setUserId("buyer@example.com");
        notification.setTitle("Payment received");
        notification.setRead(false);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(notificationRepository.findById(991L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationDTO result = buyerService.markOwnNotificationRead(991L);

        assertTrue(result.isRead());
        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markOwnNotificationRead_shouldBeIdempotentAndRejectAnotherUsersNotification() {
        Notification alreadyRead = new Notification();
        alreadyRead.setNotificationId(992L);
        alreadyRead.setUserId("buyer@example.com");
        alreadyRead.setRead(true);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(notificationRepository.findById(992L)).thenReturn(Optional.of(alreadyRead));

        buyerService.markOwnNotificationRead(992L);
        verify(notificationRepository, never()).save(alreadyRead);

        Notification otherUser = new Notification();
        otherUser.setNotificationId(993L);
        otherUser.setUserId("other@example.com");
        when(notificationRepository.findById(993L)).thenReturn(Optional.of(otherUser));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.markOwnNotificationRead(993L));
        verify(notificationRepository, never()).save(otherUser);
    }

    @Test
    void markOwnNotificationRead_shouldRejectMissingNotification() {
        when(notificationRepository.findById(994L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> buyerService.markOwnNotificationRead(994L));
    }

    @Test
    void markAllOwnNotificationsRead_shouldUpdateOnlyAuthenticatedUsersUnreadRows() {
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(notificationRepository.markAllAsReadByUserId("buyer@example.com")).thenReturn(3);

        int updated = buyerService.markAllOwnNotificationsRead();

        assertEquals(3, updated);
        verify(notificationRepository).markAllAsReadByUserId("buyer@example.com");
    }

    @Test
    void markAllOwnNotificationsRead_shouldReturnZeroWhenNothingIsUnread() {
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(notificationRepository.markAllAsReadByUserId("buyer@example.com")).thenReturn(0);

        assertEquals(0, buyerService.markAllOwnNotificationsRead());
        verify(notificationRepository).markAllAsReadByUserId("buyer@example.com");
    }

    @Test
    void contact_shouldPersistNewSubmissionWithNormalizedEmail() {
        ContactRequestDTO request = new ContactRequestDTO();
        request.setName("  Alice  ");
        request.setEmail(" Alice@Example.COM ");
        request.setMessage("I need help with my order.");
        when(contactSubmissionRepository.countByEmailIgnoreCaseAndSubmittedAtAfter(
                eq("alice@example.com"), any(LocalDateTime.class))).thenReturn(0L);
        when(contactSubmissionRepository.save(any(ContactSubmission.class))).thenAnswer(invocation -> {
            ContactSubmission submission = invocation.getArgument(0);
            submission.setContactId(1001L);
            return submission;
        });

        ContactResponseDTO result = contactService.submit(request);

        assertEquals(1001L, result.getContactId());
        assertEquals("NEW", result.getStatus());
        verify(contactSubmissionRepository).save(argThat(submission ->
                "Alice".equals(submission.getName()) && "alice@example.com".equals(submission.getEmail())));
    }

    @Test
    void contact_shouldRejectHoneypotAndRateLimitedSubmissions() {
        ContactRequestDTO honeypot = new ContactRequestDTO();
        honeypot.setName("Bot");
        honeypot.setEmail("bot@example.com");
        honeypot.setMessage("This is spam content.");
        honeypot.setWebsite("https://spam.example");
        assertThrows(IllegalArgumentException.class, () -> contactService.submit(honeypot));

        ContactRequestDTO limited = new ContactRequestDTO();
        limited.setName("Alice");
        limited.setEmail("alice@example.com");
        limited.setMessage("I still need assistance.");
        when(contactSubmissionRepository.countByEmailIgnoreCaseAndSubmittedAtAfter(
                eq("alice@example.com"), any(LocalDateTime.class))).thenReturn(3L);
        assertThrows(IllegalStateException.class, () -> contactService.submit(limited));
        verify(contactSubmissionRepository, never()).save(any(ContactSubmission.class));
    }

    @Test
    void publicPage_shouldReturnPublishedPageBySlug() {
        StaticPage page = new StaticPage();
        page.setPageId(1101L);
        page.setSlug("shipping-policy");
        page.setTitle("Shipping policy");
        page.setContent("Orders ship within two business days.");
        page.setPublished(true);
        page.setPublishedAt(LocalDateTime.now().minusMinutes(1));
        when(staticPageRepository.findBySlugIgnoreCase("shipping-policy")).thenReturn(Optional.of(page));

        StaticPageDTO result = publicPageService.getPublishedPage(" shipping-policy ");

        assertEquals(1101L, result.getPageId());
        assertEquals("Shipping policy", result.getTitle());
    }

    @Test
    void publicPage_shouldHideUnpublishedFutureAndMissingPages() {
        StaticPage unpublished = new StaticPage();
        unpublished.setSlug("faq");
        unpublished.setPublished(false);
        when(staticPageRepository.findBySlugIgnoreCase("faq")).thenReturn(Optional.of(unpublished));
        assertThrows(IllegalArgumentException.class, () -> publicPageService.getPublishedPage("faq"));

        StaticPage future = new StaticPage();
        future.setSlug("returns");
        future.setPublished(true);
        future.setPublishedAt(LocalDateTime.now().plusHours(1));
        when(staticPageRepository.findBySlugIgnoreCase("returns")).thenReturn(Optional.of(future));
        assertThrows(IllegalArgumentException.class, () -> publicPageService.getPublishedPage("returns"));

        when(staticPageRepository.findBySlugIgnoreCase("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> publicPageService.getPublishedPage("missing"));
    }

    @Test
    void adminPages_shouldReturnEditablePagesWithPublicationMetadata() {
        StaticPage page = new StaticPage();
        page.setPageId(1102L);
        page.setSlug("faq");
        page.setTitle("Frequently asked questions");
        page.setPublished(true);
        org.springframework.data.domain.Page<StaticPage> pages =
                new org.springframework.data.domain.PageImpl<>(List.of(page));
        when(staticPageRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(pages);

        var result = adminPageService.getPages(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("faq", result.getContent().get(0).getSlug());
        assertTrue(result.getContent().get(0).isPublished());
    }

    @Test
    void adminPages_shouldRejectInvalidPageParameters() {
        assertThrows(IllegalArgumentException.class, () -> adminPageService.getPages(-1, 20));
        assertThrows(IllegalArgumentException.class, () -> adminPageService.getPages(0, 101));
        verifyNoInteractions(staticPageRepository);
    }

    @Test
    void createAdminPage_shouldGenerateUniqueSlugAndSanitizeRichText() {
        AdminPageCreateDTO request = new AdminPageCreateDTO();
        request.setTitle("Shipping & Returns");
        request.setContent("<p>Fast shipping</p><script>alert('x')</script><img src='x' onerror='x'>");
        when(staticPageRepository.existsBySlugIgnoreCase("shipping-returns")).thenReturn(false);
        when(staticPageRepository.save(any(StaticPage.class))).thenAnswer(invocation -> {
            StaticPage page = invocation.getArgument(0);
            page.setPageId(1103L);
            return page;
        });

        StaticPageDTO result = adminPageService.createPage(request);

        assertEquals("shipping-returns", result.getSlug());
        assertTrue(result.getContent().contains("<p>Fast shipping</p>"));
        assertFalse(result.getContent().contains("script"));
        assertFalse(result.getContent().contains("img"));
    }

    @Test
    void createAdminPage_shouldRejectDuplicateSlug() {
        AdminPageCreateDTO request = new AdminPageCreateDTO();
        request.setSlug("faq");
        request.setTitle("FAQ");
        request.setContent("<p>Answers</p>");
        when(staticPageRepository.existsBySlugIgnoreCase("faq")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminPageService.createPage(request));
        verify(staticPageRepository, never()).save(any(StaticPage.class));
    }

    @Test
    void updateAdminPage_shouldChangePublicationAndRecordAudit() {
        StaticPage page = new StaticPage();
        page.setPageId(1104L);
        page.setSlug("faq");
        page.setTitle("FAQ");
        page.setContent("<p>Old content</p>");
        page.setPublished(false);
        when(staticPageRepository.findById(1104L)).thenReturn(Optional.of(page));
        when(staticPageRepository.save(page)).thenReturn(page);
        when(authenticationHelper.getCurrentUserId()).thenReturn("admin@example.com");
        AdminPageUpdateDTO update = new AdminPageUpdateDTO();
        update.setContent("<p>New content</p><script>bad()</script>");
        update.setPublished(true);

        StaticPageDTO result = adminPageService.updatePage(1104L, update);

        assertEquals("<p>New content</p>", result.getContent());
        assertTrue(result.isPublished());
        assertEquals("admin@example.com", result.getLastUpdatedBy());
        assertNotNull(result.getLastUpdatedAt());
        assertNotNull(result.getPublishedAt());
        verify(staticPageRepository).save(page);
    }

    @Test
    void updateAdminPage_shouldRejectDuplicateSlugAndMissingFields() {
        assertThrows(IllegalArgumentException.class,
                () -> adminPageService.updatePage(1105L, new AdminPageUpdateDTO()));
        StaticPage page = new StaticPage();
        page.setPageId(1105L);
        when(staticPageRepository.findById(1105L)).thenReturn(Optional.of(page));
        StaticPage other = new StaticPage();
        other.setPageId(1106L);
        when(staticPageRepository.findBySlugIgnoreCase("faq")).thenReturn(Optional.of(other));
        AdminPageUpdateDTO update = new AdminPageUpdateDTO();
        update.setSlug("faq");
        assertThrows(IllegalArgumentException.class, () -> adminPageService.updatePage(1105L, update));
        verify(staticPageRepository, never()).save(any(StaticPage.class));
    }

    @Test
    void adminEmailTemplates_shouldListConfigurableTemplateFields() {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateId(1301L);
        template.setTemplateKey("ORDER_CONFIRMATION");
        template.setSubject("Order {{orderId}}");
        template.setBody("Your order has been placed.");
        template.setAllowedVariables("orderId,customerName");
        template.setActive(true);
        org.springframework.data.domain.Page<EmailTemplate> templates =
                new org.springframework.data.domain.PageImpl<>(List.of(template));
        when(emailTemplateRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(templates);

        var result = adminEmailTemplateService.getTemplates(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("ORDER_CONFIRMATION", result.getContent().get(0).getTemplateKey());
        assertEquals("orderId,customerName", result.getContent().get(0).getAllowedVariables());
        assertTrue(result.getContent().get(0).isActive());
    }

    @Test
    void adminEmailTemplates_shouldRejectInvalidPageParameters() {
        assertThrows(IllegalArgumentException.class, () -> adminEmailTemplateService.getTemplates(0, 101));
        verifyNoInteractions(emailTemplateRepository);
    }

    @Test
    void updateEmailTemplate_shouldValidateVariablesAndRecordAudit() {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateId(1302L);
        template.setTemplateKey("ORDER_CONFIRMATION");
        template.setSubject("Order");
        template.setBody("Hello");
        template.setAllowedVariables("orderId,customerName");
        when(emailTemplateRepository.findById(1302L)).thenReturn(Optional.of(template));
        when(emailTemplateRepository.save(template)).thenReturn(template);
        when(authenticationHelper.getCurrentUserId()).thenReturn("admin@example.com");
        EmailTemplateUpdateDTO update = new EmailTemplateUpdateDTO();
        update.setSubject("Order {{orderId}}");
        update.setBody("Hello {{customerName}}");
        update.setActive(true);

        EmailTemplateDTO result = adminEmailTemplateService.updateTemplate(1302L, update);

        assertEquals("Order {{orderId}}", result.getSubject());
        assertEquals("Hello {{customerName}}", result.getBody());
        assertTrue(result.isActive());
        assertEquals("admin@example.com", result.getLastUpdatedBy());
        assertNotNull(result.getLastUpdatedAt());
        verify(emailTemplateRepository).save(template);
    }

    @Test
    void updateEmailTemplate_shouldRejectUndeclaredVariablesAndEmptyUpdates() {
        assertThrows(IllegalArgumentException.class,
                () -> adminEmailTemplateService.updateTemplate(1303L, new EmailTemplateUpdateDTO()));
        EmailTemplate template = new EmailTemplate();
        template.setTemplateId(1303L);
        template.setAllowedVariables("orderId");
        when(emailTemplateRepository.findById(1303L)).thenReturn(Optional.of(template));
        EmailTemplateUpdateDTO update = new EmailTemplateUpdateDTO();
        update.setBody("Hello {{customerName}}");
        assertThrows(IllegalArgumentException.class,
                () -> adminEmailTemplateService.updateTemplate(1303L, update));
        verify(emailTemplateRepository, never()).save(any(EmailTemplate.class));
    }

    @Test
    void adminEmailDeliveries_shouldListOnlyFailedAndRetryingWithMaskedRecipient() {
        EmailDelivery failed = new EmailDelivery();
        failed.setDeliveryId(1401L);
        failed.setTemplateKey("ORDER_CONFIRMATION");
        failed.setRecipient("alice@example.com");
        failed.setStatus("FAILED");
        failed.setAttempts(3);
        EmailDelivery sent = new EmailDelivery();
        sent.setDeliveryId(1402L);
        sent.setRecipient("bob@example.com");
        sent.setStatus("SENT");
        when(emailDeliveryRepository.findAll()).thenReturn(List.of(failed, sent));

        var result = adminEmailDeliveryService.getFailedDeliveries(0, 20, null);

        assertEquals(1, result.getTotalElements());
        EmailDeliveryDTO dto = result.getContent().get(0);
        assertEquals("al***@example.com", dto.getRecipientMasked());
        assertEquals(3, dto.getAttempts());
    }

    @Test
    void adminEmailDeliveries_shouldRejectUnsupportedStatusAndPageSize() {
        assertThrows(IllegalArgumentException.class,
                () -> adminEmailDeliveryService.getFailedDeliveries(0, 20, "SENT"));
        assertThrows(IllegalArgumentException.class,
                () -> adminEmailDeliveryService.getFailedDeliveries(0, 101, null));
        verifyNoInteractions(emailDeliveryRepository);
    }

    @Test
    void adminUsers_shouldReturnPaginatedRestrictedUserProfiles() {
        User user = new User();
        user.setEmail("buyer@example.com");
        user.setFirstName("Buyer");
        user.setLastName("One");
        org.springframework.data.domain.Page<User> users =
                new org.springframework.data.domain.PageImpl<>(List.of(user));
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(users);

        var result = userService.getAllUsers(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("buyer@example.com", result.getContent().get(0).getEmail());
        assertNull(result.getContent().get(0).getPassword());
    }

    @Test
    void adminUsers_shouldRejectInvalidPagination() {
        assertThrows(IllegalArgumentException.class, () -> userService.getAllUsers(0, 101));
        verifyNoInteractions(userRepository);
    }

    @Test
    void adminUserDetail_shouldReturnSafeProfileWithoutCredentialSecrets() {
        User user = new User();
        user.setEmail("detail@example.com");
        user.setFirstName("Detail");
        user.setLastName("User");
        user.setPassword("hashed-password");
        user.setRoles(List.of());
        when(userRepository.findById("detail@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById("detail@example.com");

        assertEquals("detail@example.com", result.getEmail());
        assertNull(result.getPassword());
        assertNull(result.getDob());
    }

    @Test
    void adminUserDetail_shouldRejectMissingUser() {
        when(userRepository.findById("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById("missing@example.com"));
    }

    @Test
    void updateUserRoles_shouldAssignNamedRoles() {
        User user = new User();
        user.setEmail("staff@example.com");
        Role staff = new Role();
        staff.setRoleId(1401L);
        staff.setName("STAFF");
        user.setRoles(new java.util.ArrayList<>());
        when(userRepository.findById("staff@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staff));
        when(userRepository.save(user)).thenReturn(user);
        UserRolesUpdateDTO request = new UserRolesUpdateDTO();
        request.setRoles(List.of("staff"));

        UserDTO result = roleService.updateUserRoles("staff@example.com", request.getRoles());

        assertEquals(List.of("STAFF"), result.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRoles_shouldPreventRemovingLastActiveAdmin() {
        User admin = new User();
        admin.setEmail("admin@example.com");
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        admin.setRoles(new java.util.ArrayList<>(List.of(adminRole)));
        admin.setActive(true);
        User staff = new User();
        staff.setEmail("staff@example.com");
        staff.setRoles(new java.util.ArrayList<>());
        when(userRepository.findById("admin@example.com")).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(new Role()));
        when(userRepository.findAll()).thenReturn(List.of(admin, staff));
        UserRolesUpdateDTO request = new UserRolesUpdateDTO();
        request.setRoles(List.of("STAFF"));

        assertThrows(IllegalStateException.class,
                () -> roleService.updateUserRoles("admin@example.com", request.getRoles()));
        verify(userRepository, never()).save(admin);
    }

    @Test
    void getAllRoles_shouldIncludePermissionDefinitions() {
        Permission permission = new Permission();
        permission.setPermissionId(1501L);
        permission.setName("user:read");
        Role role = new Role();
        role.setRoleId(1502L);
        role.setName("STAFF");
        role.setPermissions(List.of(permission));
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<RoleDTO> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        assertEquals("STAFF", result.get(0).getName());
        assertEquals(List.of("user:read"), result.get(0).getPermissions());
    }

    @Test
    void updateRolePermissions_shouldReplacePermissionsAndAuditChange() {
        Permission previous = new Permission();
        previous.setName("user:read");
        Permission updated = new Permission();
        updated.setName("user:update");
        Role role = new Role();
        role.setRoleId(1503L);
        role.setName("STAFF");
        role.setPermissions(new java.util.ArrayList<>(List.of(previous)));
        when(roleRepository.findById(1503L)).thenReturn(Optional.of(role));
        when(permissionRepository.findByName("user:update")).thenReturn(Optional.of(updated));
        when(roleRepository.save(role)).thenReturn(role);

        RolePermissionsUpdateDTO request = new RolePermissionsUpdateDTO();
        request.setPermissions(List.of(" user:update "));

        RoleDTO result = roleService.updateRolePermissions(1503L, request.getPermissions());

        assertEquals(List.of("user:update"), result.getPermissions());
        verify(rolePermissionAuditRepository).save(any(RolePermissionAudit.class));
    }

    @Test
    void adminSettings_shouldReturnStoreBrandingContactCurrencyAndPolicies() {
        StoreSettings settings = new StoreSettings();
        settings.setSettingsId(1L);
        settings.setStoreName("Acme Shop");
        settings.setContactEmail("support@acme.test");
        settings.setCurrency("EUR");
        settings.setShippingPolicy("Ships in 2 days");
        when(storeSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));

        AdminSettingsDTO result = storeSettingsService.getSettings();

        assertEquals("Acme Shop", result.getStoreName());
        assertEquals("support@acme.test", result.getContactEmail());
        assertEquals("EUR", result.getCurrency());
        assertEquals("Ships in 2 days", result.getShippingPolicy());
        verify(storeSettingsRepository, never()).save(any(StoreSettings.class));
    }

    @Test
    void adminSettings_shouldUpdateValidatedFieldsAndAuditChange() {
        StoreSettings settings = new StoreSettings();
        settings.setSettingsId(1L);
        settings.setStoreName("Acme Shop");
        settings.setCurrency("USD");
        when(storeSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(storeSettingsRepository.save(settings)).thenReturn(settings);
        when(orderRepository.count()).thenReturn(0L);

        AdminSettingsUpdateDTO request = new AdminSettingsUpdateDTO();
        request.setStoreName(" Updated Shop ");
        request.setCurrency("eur");

        AdminSettingsDTO result = storeSettingsService.updateSettings(request);

        assertEquals("Updated Shop", result.getStoreName());
        assertEquals("EUR", result.getCurrency());
        verify(storeSettingsAuditRepository).save(any(StoreSettingsAudit.class));
    }

    @Test
    void adminSettings_shouldRejectCurrencyChangeAfterOrdersExist() {
        StoreSettings settings = new StoreSettings();
        settings.setSettingsId(1L);
        settings.setStoreName("Acme Shop");
        settings.setCurrency("USD");
        when(storeSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(orderRepository.count()).thenReturn(1L);

        AdminSettingsUpdateDTO request = new AdminSettingsUpdateDTO();
        request.setCurrency("EUR");

        assertThrows(IllegalStateException.class, () -> storeSettingsService.updateSettings(request));
        verify(storeSettingsRepository, never()).save(any(StoreSettings.class));
        verifyNoInteractions(storeSettingsAuditRepository);
    }

    @Test
    void adminDashboard_shouldReturnDateScopedPaidPendingAndInventorySummary() {
        Order paid = new Order();
        paid.setOrderDate(java.time.LocalDate.of(2026, 1, 10));
        paid.setPaymentStatus("PAID");
        paid.setTotalPrice(100.0);
        Order pending = new Order();
        pending.setOrderDate(java.time.LocalDate.of(2026, 1, 11));
        pending.setPaymentStatus("REQUIRES_PAYMENT");
        pending.setTotalPrice(40.0);
        when(orderRepository.findByOrderDateBetween(java.time.LocalDate.of(2026, 1, 1),
                java.time.LocalDate.of(2026, 1, 31))).thenReturn(List.of(paid, pending));
        when(inventoryRepository.findLowStock(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(inventoryMovementRepository.findByOccurredAtBetweenOrderByOccurredAtDescMovementIdDesc(
                any(java.time.LocalDateTime.class), any(java.time.LocalDateTime.class),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        AdminDashboardDTO result = adminDashboardService.getDashboard(
                java.time.LocalDate.of(2026, 1, 1), java.time.LocalDate.of(2026, 1, 31));

        assertEquals(2, result.getTotalOrders());
        assertEquals(1, result.getPaidOrderCount());
        assertEquals(100.0, result.getPaidOrderTotal());
        assertEquals(1, result.getPendingOrderCount());
        assertEquals(40.0, result.getPendingOrderTotal());
        assertEquals(0, result.getLowStockItems().size());
    }

    @Test
    void adminDashboard_shouldRejectInvalidDateRange() {
        assertThrows(IllegalArgumentException.class, () -> adminDashboardService.getDashboard(
                java.time.LocalDate.of(2026, 2, 1), java.time.LocalDate.of(2026, 1, 1)));
        verifyNoInteractions(orderRepository, inventoryRepository, inventoryMovementRepository);
    }

    @Test
    void orderReport_shouldReturnBoundedEscapedCsvForDateRange() {
        Order order = new Order();
        order.setOrderId(1510L);
        order.setOrderDate(java.time.LocalDate.of(2026, 2, 10));
        order.setOrderStatus("PAID,READY");
        order.setPaymentStatus("PAID");
        order.setTotalPrice(125.5);
        User customer = new User();
        customer.setEmail("buyer@example.com");
        order.setUser(customer);
        when(orderRepository.findByOrderDateBetween(eq(java.time.LocalDate.of(2026, 2, 1)),
                eq(java.time.LocalDate.of(2026, 2, 28)), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(order)));

        String csv = reportService.getOrderReportCsv(java.time.LocalDate.of(2026, 2, 1),
                java.time.LocalDate.of(2026, 2, 28));

        assertTrue(csv.startsWith("orderId,orderDate,orderStatus,paymentStatus,totalPrice,customerEmail\n"));
        assertTrue(csv.contains("1510,2026-02-10,\"PAID,READY\",PAID,125.5,buyer@example.com"));
    }

    @Test
    void inventoryReport_shouldReturnSafeEscapedCsv() {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1520L);
        inventory.setStockAvailable(3);
        inventory.setReservedQuantity(2);
        inventory.setLowStockThreshold(5);
        inventory.setSellingPrice(19.95);
        SKU sku = new SKU();
        sku.setSkuId(1521L);
        sku.setSkuCode("SKU,\"1521");
        inventory.setSku(sku);
        when(inventoryRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(inventory)));

        String csv = reportService.getInventoryReportCsv();

        assertTrue(csv.startsWith("inventoryId,variantId,skuCode,onHand,reserved,available,lowStockThreshold,lowStock,sellingPrice\n"));
        assertTrue(csv.contains("1520,1521,\"SKU,\"\"1521\",5,2,3,5,true,19.95"));
    }

    @Test
    void adminAudit_shouldReturnPaginatedRedactedEvents() {
        RolePermissionAudit roleAudit = new RolePermissionAudit();
        roleAudit.setAuditId(1530L);
        roleAudit.setRoleId(12L);
        roleAudit.setRoleName("STAFF");
        roleAudit.setChangedBy("admin@example.com");
        roleAudit.setChangedAt(java.time.Instant.parse("2026-03-01T10:00:00Z"));
        roleAudit.setPreviousPermissions("secret:old");
        roleAudit.setUpdatedPermissions("secret:new");
        when(rolePermissionAuditRepository.findAll()).thenReturn(List.of(roleAudit));
        when(storeSettingsAuditRepository.findAll()).thenReturn(List.of());

        org.springframework.data.domain.Page<AdminAuditDTO> result = adminAuditService.getAudit(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("UPDATE_PERMISSIONS", result.getContent().get(0).getAction());
        assertEquals("admin@example.com", result.getContent().get(0).getActor());
        assertTrue(result.getContent().get(0).getSummary().contains("STAFF"));
        assertFalse(result.getContent().get(0).getSummary().contains("secret"));
    }

    @Test
    void unpaidOrderExpiry_shouldReleaseReservationExactlyOnceAndPublishEvent() {
        SKU sku = new SKU();
        sku.setSkuId(1561L);
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1562L);
        inventory.setSku(sku);
        inventory.setStockAvailable(4);
        inventory.setReservedQuantity(2);
        OrderItem item = new OrderItem();
        item.setSku(sku);
        item.setQuantity(2);
        Order order = new Order();
        order.setOrderId(1563L);
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setOrderStatus("PENDING");
        order.setReservationExpiresAt(java.time.LocalDateTime.of(2026, 3, 1, 9, 0));
        order.setOrderItems(List.of(item));
        when(orderRepository.findByReservationExpiresAtLessThanEqualAndPaymentStatusNotIn(
                any(java.time.LocalDateTime.class), any(Collection.class))).thenReturn(List.of(order));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));
        when(orderRepository.save(order)).thenReturn(order);

        int expired = unpaidOrderExpiryService.expireUnpaidOrders(
                java.time.LocalDateTime.of(2026, 3, 1, 10, 0));

        assertEquals(1, expired);
        assertEquals("EXPIRED", order.getPaymentStatus());
        assertEquals(6, inventory.getStockAvailable());
        assertEquals(0, inventory.getReservedQuantity());
        verify(inventoryRepository).save(inventory);
        verify(outboxService).enqueue(any(OrderEvent.class));
    }

    @Test
    void transactionalOutbox_shouldDeduplicateAndMarkPublished() throws Exception {
        OrderEvent event = new OrderEvent(1570L, OrderEvent.Status.PENDING, null);
        OutboxEvent stored = new OutboxEvent();
        stored.setDeduplicationKey("order:1570:PENDING");
        stored.setStatus("PENDING");
        stored.setPayload("{}");
        stored.setAttempts(0);
        when(outboxEventRepository.findByDeduplicationKey("order:1570:PENDING"))
                .thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(event)).thenReturn("{}");
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(stored);

        transactionalOutboxService.enqueue(event);

        when(outboxEventRepository.findByDeduplicationKey("order:1570:PENDING"))
                .thenReturn(Optional.of(stored));
        assertSame(stored, transactionalOutboxService.enqueue(event));
        verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));

        when(objectMapper.readValue("{}", OrderEvent.class)).thenReturn(event);
        when(orderProducerService.publish(event)).thenReturn(true);
        transactionalOutboxService.dispatchOne(stored);

        assertEquals("PUBLISHED", stored.getStatus());
        assertNotNull(stored.getPublishedAt());
    }

    @Test
    void retryEmailDelivery_shouldIncrementAttemptsAndDeduplicateInProgressJobs() {
        EmailDelivery delivery = new EmailDelivery();
        delivery.setDeliveryId(1403L);
        delivery.setStatus("FAILED");
        delivery.setAttempts(2);
        when(emailDeliveryRepository.findById(1403L)).thenReturn(Optional.of(delivery));
        when(emailDeliveryRepository.save(delivery)).thenReturn(delivery);

        EmailDeliveryDTO result = adminEmailDeliveryService.retry(1403L);

        assertEquals("RETRYING", result.getStatus());
        assertEquals(3, result.getAttempts());
        assertNotNull(result.getNextAttemptAt());
        verify(emailDeliveryRepository).save(delivery);

        delivery.setStatus("RETRYING");
        assertThrows(IllegalStateException.class, () -> adminEmailDeliveryService.retry(1403L));
    }

    @Test
    void retryEmailDelivery_shouldRejectSentAndExhaustedJobs() {
        EmailDelivery sent = new EmailDelivery();
        sent.setDeliveryId(1404L);
        sent.setStatus("SENT");
        when(emailDeliveryRepository.findById(1404L)).thenReturn(Optional.of(sent));
        assertThrows(IllegalStateException.class, () -> adminEmailDeliveryService.retry(1404L));

        EmailDelivery exhausted = new EmailDelivery();
        exhausted.setDeliveryId(1405L);
        exhausted.setStatus("FAILED");
        exhausted.setAttempts(5);
        when(emailDeliveryRepository.findById(1405L)).thenReturn(Optional.of(exhausted));
        assertThrows(IllegalStateException.class, () -> adminEmailDeliveryService.retry(1405L));
        verify(emailDeliveryRepository, never()).save(exhausted);
    }

    @Test
    void notificationDelivery_shouldDeduplicateEmailAndRetainFailedStatus() {
        EmailDelivery delivery = new EmailDelivery();
        delivery.setStatus("PENDING");
        delivery.setAttempts(4);
        delivery.setRecipient("buyer@example.com");
        delivery.setSubject("Order");
        delivery.setBody("Order update");
        when(emailDeliveryRepository.findByDeduplicationKey("order:1580"))
                .thenReturn(Optional.empty());
        when(emailDeliveryRepository.save(any(EmailDelivery.class))).thenReturn(delivery);

        EmailDelivery created = notificationDeliveryService.enqueueEmail(
                "ORDER", "buyer@example.com", "Order", "Order update", "order:1580");

        assertSame(delivery, created);
        doThrow(new IllegalStateException("SMTP unavailable"))
                .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        notificationDeliveryService.deliverEmail(delivery);

        assertEquals("FAILED", delivery.getStatus());
        assertEquals(5, delivery.getAttempts());
        assertTrue(delivery.getLastError().contains("SMTP unavailable"));
    }

    @Test
    void ownTickets_shouldReturnOnlyPagedAuthenticatedUsersTickets() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1201L);
        ticket.setUserId("buyer@example.com");
        ticket.setSubject("Order issue");
        ticket.setDescription("My package is delayed.");
        ticket.setPriority("NORMAL");
        ticket.setStatus("OPEN");
        org.springframework.data.domain.Page<Ticket> page =
                new org.springframework.data.domain.PageImpl<>(List.of(ticket));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.findByUserIdOrderByCreatedAtDesc(
                eq("buyer@example.com"), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        var result = ticketService.getOwnTickets(0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals("Order issue", result.getContent().get(0).getSubject());
        verify(ticketRepository).findByUserIdOrderByCreatedAtDesc(
                eq("buyer@example.com"), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void ownTickets_shouldRejectInvalidPageParameters() {
        assertThrows(IllegalArgumentException.class, () -> ticketService.getOwnTickets(-1, 20));
        assertThrows(IllegalArgumentException.class, () -> ticketService.getOwnTickets(0, 101));
        verifyNoInteractions(ticketRepository);
    }

    @Test
    void createTicket_shouldAssignAuthenticatedOwnerAndDefaultPriority() {
        TicketCreateDTO request = new TicketCreateDTO();
        request.setSubject("  Order issue  ");
        request.setDescription("My order has not arrived yet.");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setTicketId(1202L);
            return ticket;
        });

        TicketDTO result = ticketService.createTicket(request);

        assertEquals(1202L, result.getTicketId());
        assertEquals("Order issue", result.getSubject());
        assertEquals("NORMAL", result.getPriority());
        assertEquals("OPEN", result.getStatus());
        verify(ticketRepository).save(argThat(ticket ->
                "buyer@example.com".equals(ticket.getUserId())
                        && "Order issue".equals(ticket.getSubject())));
    }

    @Test
    void createTicket_shouldAcceptValidPriorityAndRejectInvalidInput() {
        TicketCreateDTO high = new TicketCreateDTO();
        high.setSubject("Urgent order issue");
        high.setDescription("Please resolve this delivery issue quickly.");
        high.setPriority("high");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("HIGH", ticketService.createTicket(high).getPriority());

        TicketCreateDTO invalid = new TicketCreateDTO();
        invalid.setSubject("Invalid priority");
        invalid.setDescription("This request has an unsupported priority.");
        invalid.setPriority("CRITICAL");
        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(invalid));
    }

    @Test
    void ownTicket_shouldReturnDetailsAndChronologicalThread() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1203L);
        ticket.setUserId("buyer@example.com");
        ticket.setSubject("Delivery issue");
        ticket.setStatus("OPEN");
        TicketMessage first = new TicketMessage();
        first.setMessageId(1204L);
        first.setTicketId(1203L);
        first.setAuthorType("CUSTOMER");
        first.setMessage("The package is late.");
        TicketMessage second = new TicketMessage();
        second.setMessageId(1205L);
        second.setTicketId(1203L);
        second.setAuthorType("STAFF");
        second.setMessage("We are checking this now.");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.findById(1203L)).thenReturn(Optional.of(ticket));
        when(ticketMessageRepository.findByTicketIdOrderByCreatedAtAscMessageIdAsc(1203L))
                .thenReturn(List.of(first, second));

        TicketDetailDTO result = ticketService.getOwnTicket(1203L);

        assertEquals("Delivery issue", result.getSubject());
        assertEquals(2, result.getMessages().size());
        assertEquals("CUSTOMER", result.getMessages().get(0).getAuthorType());
        assertEquals("STAFF", result.getMessages().get(1).getAuthorType());
    }

    @Test
    void ownTicket_shouldRejectAnotherUsersOrMissingTicket() {
        Ticket other = new Ticket();
        other.setTicketId(1206L);
        other.setUserId("other@example.com");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.findById(1206L)).thenReturn(Optional.of(other));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> ticketService.getOwnTicket(1206L));
        verifyNoInteractions(ticketMessageRepository);

        when(ticketRepository.findById(1207L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> ticketService.getOwnTicket(1207L));
    }

    @Test
    void addOwnTicketMessage_shouldCreateCustomerMessageUpdateTicketAndNotify() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1208L);
        ticket.setUserId("buyer@example.com");
        ticket.setStatus("CLOSED");
        TicketMessageCreateDTO request = new TicketMessageCreateDTO();
        request.setMessage("Please reopen this ticket.");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.findById(1208L)).thenReturn(Optional.of(ticket));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> {
            TicketMessage message = invocation.getArgument(0);
            message.setMessageId(1209L);
            return message;
        });
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketMessageDTO result = ticketService.addOwnTicketMessage(1208L, request);

        assertEquals(1209L, result.getMessageId());
        assertEquals("CUSTOMER", result.getAuthorType());
        assertEquals("OPEN", ticket.getStatus());
        verify(ticketRepository).save(ticket);
        verify(notificationRepository).save(argThat(notification ->
                "TICKET_REPLY".equals(notification.getType())
                        && "buyer@example.com".equals(notification.getUserId())));
    }

    @Test
    void addOwnTicketMessage_shouldRejectOtherOwnersAndBlankMessages() {
        Ticket other = new Ticket();
        other.setTicketId(1210L);
        other.setUserId("other@example.com");
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(ticketRepository.findById(1210L)).thenReturn(Optional.of(other));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> ticketService.addOwnTicketMessage(1210L, new TicketMessageCreateDTO()));

        TicketMessageCreateDTO request = new TicketMessageCreateDTO();
        request.setMessage(" ");
        Ticket own = new Ticket();
        own.setTicketId(1211L);
        own.setUserId("buyer@example.com");
        when(ticketRepository.findById(1211L)).thenReturn(Optional.of(own));
        assertThrows(IllegalArgumentException.class, () -> ticketService.addOwnTicketMessage(1211L, request));
        verifyNoInteractions(ticketMessageRepository, notificationRepository);
    }

    @Test
    void guestTicketVerification_shouldSendGenericResponseOnlyForMatchingGuestEmail() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1212L);
        ticket.setGuestEmail("guest@example.com");
        when(ticketRepository.findById(1212L)).thenReturn(Optional.of(ticket));
        GuestTicketVerificationRequest request = new GuestTicketVerificationRequest();
        request.setTicketId(1212L);
        request.setEmail(" GUEST@example.com ");

        GuestTicketVerificationResponse result = guestTicketVerificationService.request(request);

        assertTrue(result.getMessage().contains("If an eligible guest ticket exists"));
        verify(emailService).sendGuestTicketVerificationEmail(eq("guest@example.com"), anyString());

        request.setTicketId(1213L);
        when(ticketRepository.findById(1213L)).thenReturn(Optional.empty());
        guestTicketVerificationService.request(request);
        verify(emailService, times(1)).sendGuestTicketVerificationEmail(anyString(), anyString());
    }

    @Test
    void guestTicketVerification_shouldRejectMalformedRequest() {
        assertThrows(IllegalArgumentException.class,
                () -> guestTicketVerificationService.request(new GuestTicketVerificationRequest()));
        verifyNoInteractions(ticketRepository, emailService);
    }

    @Test
    void guestTicketVerification_shouldIssueScopedShortLivedAccessAfterValidCode() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1214L);
        ticket.setGuestEmail("guest@example.com");
        when(ticketRepository.findById(1214L)).thenReturn(Optional.of(ticket));
        GuestTicketVerificationRequest request = new GuestTicketVerificationRequest();
        request.setTicketId(1214L);
        request.setEmail("guest@example.com");
        guestTicketVerificationService.request(request);

        org.mockito.ArgumentCaptor<String> codeCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendGuestTicketVerificationEmail(eq("guest@example.com"), codeCaptor.capture());
        GuestTicketVerificationVerifyRequest verifyRequest = new GuestTicketVerificationVerifyRequest();
        verifyRequest.setTicketId(1214L);
        verifyRequest.setCode(codeCaptor.getValue());

        GuestTicketAccessDTO access = guestTicketVerificationService.verify(verifyRequest);

        assertEquals(1214L, access.getTicketId());
        assertTrue(guestTicketVerificationService.hasAccess(1214L, access.getAccessToken()));
        assertFalse(guestTicketVerificationService.hasAccess(9999L, access.getAccessToken()));
        assertThrows(IllegalArgumentException.class, () -> guestTicketVerificationService.verify(verifyRequest));
    }

    @Test
    void guestTicketVerification_shouldRejectInvalidCode() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1215L);
        when(ticketRepository.findById(1215L)).thenReturn(Optional.of(ticket));
        GuestTicketVerificationVerifyRequest request = new GuestTicketVerificationVerifyRequest();
        request.setTicketId(1215L);
        request.setCode("000000");
        assertThrows(IllegalArgumentException.class, () -> guestTicketVerificationService.verify(request));
    }

    @Test
    void adminTickets_shouldFilterByStatusAndAssignmentAndPageResults() {
        Ticket assigned = new Ticket();
        assigned.setTicketId(1216L);
        assigned.setUserId("buyer@example.com");
        assigned.setSubject("Assigned issue");
        assigned.setStatus("OPEN");
        assigned.setAssignedTo("agent@example.com");
        Ticket other = new Ticket();
        other.setTicketId(1217L);
        other.setUserId("other@example.com");
        other.setSubject("Other issue");
        other.setStatus("CLOSED");
        when(ticketRepository.findAll()).thenReturn(List.of(assigned, other));

        var result = adminTicketService.getTickets(0, 20, "open", "AGENT@example.com");

        assertEquals(1, result.getTotalElements());
        assertEquals(1216L, result.getContent().get(0).getTicketId());
        assertEquals("agent@example.com", result.getContent().get(0).getAssignedTo());
    }

    @Test
    void adminTickets_shouldRejectInvalidPageParameters() {
        assertThrows(IllegalArgumentException.class, () -> adminTicketService.getTickets(0, 101, null, null));
        verifyNoInteractions(ticketRepository);
    }

    @Test
    void adminTicketUpdate_shouldChangePriorityStatusAssigneeAndRecordAudit() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1218L);
        ticket.setStatus("OPEN");
        ticket.setPriority("NORMAL");
        when(ticketRepository.findById(1218L)).thenReturn(Optional.of(ticket));
        when(authenticationHelper.getCurrentUserId()).thenReturn("agent@example.com");
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        AdminTicketUpdateDTO update = new AdminTicketUpdateDTO();
        update.setPriority("high");
        update.setStatus("in_progress");
        update.setAssignedTo("agent@example.com");

        AdminTicketDTO result = adminTicketService.updateTicket(1218L, update);

        assertEquals("HIGH", result.getPriority());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("agent@example.com", result.getAssignedTo());
        assertEquals("agent@example.com", result.getLastUpdatedBy());
        assertNotNull(result.getLastUpdatedAt());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void adminTicketUpdate_shouldRejectInvalidOrMissingFields() {
        assertThrows(IllegalArgumentException.class,
                () -> adminTicketService.updateTicket(1219L, new AdminTicketUpdateDTO()));
        AdminTicketUpdateDTO invalid = new AdminTicketUpdateDTO();
        invalid.setStatus("UNKNOWN");
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(1219L)).thenReturn(Optional.of(ticket));
        assertThrows(IllegalArgumentException.class, () -> adminTicketService.updateTicket(1219L, invalid));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void adminStaffMessage_shouldNotifyCustomerAndMoveTicketToWaitingForCustomer() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1220L);
        ticket.setUserId("buyer@example.com");
        ticket.setStatus("IN_PROGRESS");
        AdminTicketMessageCreateDTO request = new AdminTicketMessageCreateDTO();
        request.setMessage("We have requested a replacement.");
        when(authenticationHelper.getCurrentUserId()).thenReturn("agent@example.com");
        when(ticketRepository.findById(1220L)).thenReturn(Optional.of(ticket));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> {
            TicketMessage message = invocation.getArgument(0);
            message.setMessageId(1221L);
            return message;
        });
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        TicketMessageDTO result = adminTicketService.addStaffMessage(1220L, request);

        assertEquals("STAFF", result.getAuthorType());
        assertFalse(result.isInternalNote());
        assertEquals("WAITING_FOR_CUSTOMER", ticket.getStatus());
        verify(notificationRepository).save(argThat(notification ->
                "buyer@example.com".equals(notification.getUserId())));
        verify(ticketRepository).save(ticket);
    }

    @Test
    void adminInternalNote_shouldNotNotifyCustomerOrChangeCustomerState() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1222L);
        ticket.setUserId("buyer@example.com");
        ticket.setStatus("IN_PROGRESS");
        AdminTicketMessageCreateDTO request = new AdminTicketMessageCreateDTO();
        request.setMessage("Check warehouse records.");
        request.setInternalNote(true);
        when(authenticationHelper.getCurrentUserId()).thenReturn("agent@example.com");
        when(ticketRepository.findById(1222L)).thenReturn(Optional.of(ticket));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        TicketMessageDTO result = adminTicketService.addStaffMessage(1222L, request);

        assertEquals("INTERNAL_NOTE", result.getAuthorType());
        assertTrue(result.isInternalNote());
        assertEquals("IN_PROGRESS", ticket.getStatus());
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void adminOrderDetail_shouldRejectMissingOrder() {
        when(orderRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminService.getAdminOrder(9999L));
    }

    @Test
    void fulfillmentStart_shouldMovePaidOrderToPickingAndAuditTransition() {
        Order order = savedOrder(873L);
        order.setOrderStatus("PAID");
        order.setPaymentStatus("PAID");
        when(orderRepository.findById(873L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO result = adminService.startFulfillment(873L);

        assertEquals("PICKING", result.getOrderStatus());
        assertEquals("PICKING", order.getStatusHistory().get(0).getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void fulfillmentStart_shouldRejectUnpaidAndCancelledOrders() {
        Order unpaid = savedOrder(874L);
        unpaid.setOrderStatus("PAID");
        when(orderRepository.findById(874L)).thenReturn(Optional.of(unpaid));
        assertThrows(IllegalStateException.class, () -> adminService.startFulfillment(874L));

        Order cancelled = savedOrder(875L);
        cancelled.setOrderStatus("CANCELLED");
        cancelled.setPaymentStatus("PAID");
        when(orderRepository.findById(875L)).thenReturn(Optional.of(cancelled));
        assertThrows(IllegalStateException.class, () -> adminService.startFulfillment(875L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void fulfillmentStart_shouldBeIdempotentForPickingOrder() {
        Order picking = savedOrder(876L);
        picking.setOrderStatus("PICKING");
        when(orderRepository.findById(876L)).thenReturn(Optional.of(picking));

        OrderDTO result = adminService.startFulfillment(876L);

        assertEquals("PICKING", result.getOrderStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void fulfillmentPack_shouldRequireFullQuantitiesAndMovePickingOrderToPacked() {
        Order order = savedOrder(877L);
        order.setOrderStatus("PICKING");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-PACK-1");
        OrderItem item = new OrderItem(sku, 2, 12.0);
        item.setOrderItemId(501L);
        item.setOrder(order);
        order.setOrderItems(List.of(item));
        when(orderRepository.findById(877L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO result = adminService.packFulfillment(877L, Map.of(501L, 2));

        assertEquals("PACKED", result.getOrderStatus());
        assertEquals("PACKED", order.getStatusHistory().get(0).getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void fulfillmentPack_shouldRejectPartialQuantitiesAndInvalidState() {
        Order order = savedOrder(878L);
        order.setOrderStatus("PICKING");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-PACK-2");
        OrderItem item = new OrderItem(sku, 3, 12.0);
        item.setOrderItemId(502L);
        item.setOrder(order);
        order.setOrderItems(List.of(item));
        when(orderRepository.findById(878L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class,
                () -> adminService.packFulfillment(878L, Map.of(502L, 2)));
        order.setOrderStatus("PAID");
        assertThrows(IllegalStateException.class,
                () -> adminService.packFulfillment(878L, Map.of(502L, 3)));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void fulfillmentShip_shouldStoreShipmentAndMarkPackedOrderShipped() {
        Order order = savedOrder(879L);
        order.setOrderStatus("PACKED");
        when(orderRepository.findById(879L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO result = adminService.shipFulfillment(879L, "DHL", "DHL-879");

        assertEquals("SHIPPED", result.getOrderStatus());
        assertEquals("DHL", order.getShipmentCarrier());
        assertEquals("DHL-879", order.getTrackingNumber());
        assertNotNull(order.getShippedAt());
        assertEquals("SHIPPED", order.getStatusHistory().get(0).getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void fulfillmentShip_shouldRejectMissingShipmentDataAndUnpackedOrder() {
        Order packed = savedOrder(880L);
        packed.setOrderStatus("PACKED");
        when(orderRepository.findById(880L)).thenReturn(Optional.of(packed));
        assertThrows(IllegalArgumentException.class, () -> adminService.shipFulfillment(880L, "DHL", ""));

        Order picking = savedOrder(881L);
        picking.setOrderStatus("PICKING");
        when(orderRepository.findById(881L)).thenReturn(Optional.of(picking));
        assertThrows(IllegalStateException.class, () -> adminService.shipFulfillment(881L, "DHL", "DHL-881"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void fulfillmentDeliver_shouldMarkShippedOrderDeliveredAndNotifyCustomer() {
        User customer = new User();
        customer.setEmail("buyer@example.com");
        Order order = savedOrder(882L);
        order.setOrderStatus("SHIPPED");
        order.setUser(customer);
        when(orderRepository.findById(882L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO result = adminService.deliverFulfillment(882L);

        assertEquals("DELIVERED", result.getOrderStatus());
        assertNotNull(order.getDeliveredAt());
        assertEquals("DELIVERED", order.getStatusHistory().get(0).getStatus());
        verify(emailService).sendOrderConfirmation("buyer@example.com", 882L);
        verify(orderRepository).save(order);
    }

    @Test
    void fulfillmentDeliver_shouldRejectUnshippedOrderAndBeIdempotent() {
        Order picking = savedOrder(883L);
        picking.setOrderStatus("PACKED");
        when(orderRepository.findById(883L)).thenReturn(Optional.of(picking));
        assertThrows(IllegalStateException.class, () -> adminService.deliverFulfillment(883L));

        Order delivered = savedOrder(884L);
        delivered.setOrderStatus("DELIVERED");
        when(orderRepository.findById(884L)).thenReturn(Optional.of(delivered));
        OrderDTO result = adminService.deliverFulfillment(884L);
        assertEquals("DELIVERED", result.getOrderStatus());
        verify(emailService, never()).sendOrderConfirmation(anyString(), anyLong());
    }

    @Test
    void currentUserOrders_shouldPageOwnOrdersAndApplyStatusFilter() {
        Order paid = savedOrder(801L);
        paid.setOrderDate(java.time.LocalDate.of(2026, 10, 1));
        Order pending = savedOrder(802L);
        pending.setOrderStatus("PENDING");
        pending.setOrderDate(java.time.LocalDate.of(2026, 10, 2));

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findByUser_email(eq("buyer@example.com"), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(paid, pending)));
        when(orderRepository.findByUser_emailAndOrderStatusIgnoreCase(
                eq("buyer@example.com"), eq("PAID"), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(paid)));

        var all = buyerService.getCurrentUserOrders(0, 10, null);
        assertEquals(2, all.getTotalElements());
        assertEquals(2, all.getContent().size());

        var filtered = buyerService.getCurrentUserOrders(1, 5, " paid ");
        assertEquals(1, filtered.getTotalElements());
        assertEquals(801L, filtered.getContent().get(0).getOrderId());

        assertThrows(IllegalArgumentException.class,
                () -> buyerService.getCurrentUserOrders(0, 0, null));
        verify(orderRepository).findByUser_emailAndOrderStatusIgnoreCase(
                eq("buyer@example.com"), eq("PAID"), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void orderDetail_shouldReturnOwnOrderStateAndRejectAnotherUsersOrder() {
        User owner = new User();
        owner.setEmail("buyer@example.com");
        Order own = savedOrder(810L);
        own.setUser(owner);
        own.setPaymentStatus("PAID");
        own.setStatusHistory(List.of(new com.example.onlineshop.order.entity.OrderStatusHistory(
                own, "PENDING", "Order placed")));

        User other = new User();
        other.setEmail("other@example.com");
        Order foreign = savedOrder(811L);
        foreign.setUser(other);

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(810L)).thenReturn(Optional.of(own));
        when(orderRepository.findById(811L)).thenReturn(Optional.of(foreign));

        OrderDTO detail = buyerService.getOrderById(810L);
        assertEquals(810L, detail.getOrderId());
        assertEquals("PAID", detail.getPaymentStatus());
        assertEquals(1, detail.getStatusHistory().size());
        assertEquals("PENDING", detail.getStatusHistory().get(0).getStatus());

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> buyerService.getOrderById(811L));
    }

    @Test
    void orderReceipt_shouldExposeStoredFinancialSnapshotAndRejectForeignOrder() {
        User owner = new User();
        owner.setEmail("buyer@example.com");
        Order order = savedOrder(820L);
        order.setUser(owner);
        order.setSubtotal(80.0);
        order.setDiscount(8.0);
        order.setShippingFee(5.0);
        order.setTax(6.16);
        order.setTotalPrice(83.16);
        order.setPaymentStatus("PAID");

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(820L)).thenReturn(Optional.of(order));

        OrderReceiptDTO receipt = buyerService.getOrderReceipt(820L);

        assertEquals(820L, receipt.getOrderId());
        assertEquals(80.0, receipt.getSubtotal());
        assertEquals(8.0, receipt.getDiscount());
        assertEquals(83.16, receipt.getTotalPrice());
        assertEquals("PAID", receipt.getPaymentStatus());
        assertNotNull(receipt.getItems());
    }

    @Test
    void orderCancellation_shouldReleaseReservationAuditTransitionAndBeIdempotent() {
        User owner = new User();
        owner.setEmail("buyer@example.com");
        SKU sku = new SKU();
        sku.setSkuCode("SKU-CANCEL-1");
        Inventory inventory = new Inventory();
        inventory.setInventoryId(992L);
        inventory.setSku(sku);
        inventory.setStockAvailable(3);
        inventory.setReservedQuantity(2);

        Order order = savedOrder(830L);
        order.setUser(owner);
        order.setOrderStatus("FULFILLED");
        OrderItem item = new OrderItem(sku, 2, 15.0);
        item.setOrder(order);
        order.setOrderItems(List.of(item));

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(830L)).thenReturn(Optional.of(order));
        when(inventoryRepository.findBySku(sku)).thenReturn(List.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO cancelled = buyerService.cancelCurrentUserOrder(830L);

        assertEquals("CANCELLED", cancelled.getOrderStatus());
        assertEquals(5, inventory.getStockAvailable());
        assertEquals(0, inventory.getReservedQuantity());
        assertTrue(cancelled.getStatusHistory().stream().anyMatch(history -> "CANCELLED".equals(history.getStatus())));
        verify(inventoryRepository, times(1)).save(inventory);

        buyerService.cancelCurrentUserOrder(830L);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void orderCancellation_shouldRejectPaidOrders() {
        User owner = new User();
        owner.setEmail("buyer@example.com");
        Order order = savedOrder(831L);
        order.setUser(owner);
        order.setOrderStatus("PAID");

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(831L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> buyerService.cancelCurrentUserOrder(831L));
        verify(orderRepository, never()).save(order);
    }

    @Test
    void reorder_shouldAddAvailableVariantsUsingCurrentStockAndReportUnavailableItems() {
        User owner = new User();
        owner.setEmail("buyer@example.com");
        Product product = new Product();
        product.setApproved(true);

        SKU availableSku = new SKU();
        availableSku.setSkuCode("SKU-REORDER-AVAILABLE");
        availableSku.setProduct(product);
        Inventory available = new Inventory();
        available.setInventoryId(993L);
        available.setSku(availableSku);
        available.setSellingPrice(21.0);
        available.setStockAvailable(4);

        SKU unavailableSku = new SKU();
        unavailableSku.setSkuCode("SKU-REORDER-OUT");
        unavailableSku.setProduct(product);
        Inventory unavailable = new Inventory();
        unavailable.setInventoryId(994L);
        unavailable.setSku(unavailableSku);
        unavailable.setStockAvailable(0);

        Order order = savedOrder(840L);
        order.setUser(owner);
        OrderItem availableItem = new OrderItem(availableSku, 2, 18.0);
        availableItem.setOrder(order);
        OrderItem unavailableItem = new OrderItem(unavailableSku, 1, 12.0);
        unavailableItem.setOrder(order);
        order.setOrderItems(List.of(availableItem, unavailableItem));

        Cart cart = new Cart();
        cart.setUser(owner);
        cart.setCartItems(new java.util.ArrayList<>());

        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(840L)).thenReturn(Optional.of(order));
        when(cartRepository.findByUserEmail("buyer@example.com")).thenReturn(cart);
        when(inventoryRepository.findBySku(availableSku)).thenReturn(List.of(available));
        when(inventoryRepository.findBySku(unavailableSku)).thenReturn(List.of(unavailable));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(cart)).thenReturn(cart);

        ReorderResultDTO result = buyerService.reorderOrder(840L);

        assertEquals(List.of("SKU-REORDER-AVAILABLE"), result.getAddedSkuCodes());
        assertEquals(List.of("SKU-REORDER-OUT"), result.getUnavailableSkuCodes());
        assertEquals(1, cart.getCartItems().size());
        assertEquals(2, cart.getCartItems().get(0).getQuantity());
        assertEquals(21.0, cart.getCartItems().get(0).getInventory().getSellingPrice());
    }

    private static OrderRequestDTO.OrderItemRequestDTO itemRequest(Long inventoryId, int quantity) {
        OrderRequestDTO.OrderItemRequestDTO item = new OrderRequestDTO.OrderItemRequestDTO();
        item.setInventoryId(inventoryId);
        item.setQuantity(quantity);
        return item;
    }

    private static Order savedOrder(Long orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus("PAID");
        return order;
    }

    @Test
    void adminCoupons_shouldListCurrentActiveCouponsAndFilterByStatus() {
        com.example.onlineshop.promotion.entity.Coupon active = new com.example.onlineshop.promotion.entity.Coupon();
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

        com.example.onlineshop.promotion.entity.Coupon saved = new com.example.onlineshop.promotion.entity.Coupon();
        saved.setCouponId(1002L);
        saved.setCode("VIP20");
        saved.setDescription("VIP seasonal discount");
        saved.setType("PERCENTAGE");
        saved.setValue(20.0);
        saved.setMinSpend(50.0);
        saved.setActive(true);
        when(couponRepository.save(any(com.example.onlineshop.promotion.entity.Coupon.class))).thenReturn(saved);

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
        com.example.onlineshop.promotion.entity.Coupon coupon = new com.example.onlineshop.promotion.entity.Coupon();
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
