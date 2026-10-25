package com.example.onlineshop.service;

import com.example.onlineshop.dto.*;
import com.example.onlineshop.model.*;
import com.example.onlineshop.repository.*;
import com.example.onlineshop.util.AuthenticationHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BuyerService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;  // New repository to handle subcategories

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private WishListRepository wishListRepository;

    private static final Set<String> processedGuestCartTokens = ConcurrentHashMap.newKeySet();

    // Product Browsing with Category and Subcategory Hierarchy
    @Transactional
    public List<ProductDTO> getAllProducts() {
        return getProducts(null, null, null, null, null, "featured");
    }

    @Transactional
    public List<ProductDTO> getProducts(String category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Boolean available, String sort) {
        String normalizedSort = sort == null ? "featured" : sort.trim().toLowerCase(Locale.ROOT);

        return productRepository.findAll().stream()
                .filter(product -> product != null)
                .filter(product -> product.isApproved())
                .filter(product -> Boolean.TRUE.equals(available) ? product.isAvailable() : true)
                .filter(product -> category == null || category.isBlank() || matchesCategory(product, category))
                .filter(product -> brand == null || brand.isBlank() || matchesBrand(product, brand))
                .filter(product -> minPrice == null || hasPriceAtOrAbove(product, minPrice))
                .filter(product -> maxPrice == null || hasPriceAtOrBelow(product, maxPrice))
                .sorted((left, right) -> {
                    if ("price_asc".equals(normalizedSort)) {
                        return Double.compare(minPriceFor(left), minPriceFor(right));
                    }
                    if ("price_desc".equals(normalizedSort)) {
                        return Double.compare(minPriceFor(right), minPriceFor(left));
                    }
                    if ("name_asc".equals(normalizedSort)) {
                        return left.getName().compareToIgnoreCase(right.getName());
                    }
                    return left.getProductId().compareTo(right.getProductId());
                })
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Product product, String category) {
        if (product.getCategory() == null) {
            return false;
        }
        String normalizedCategory = category.trim();
        return product.getCategory().getCategory().equalsIgnoreCase(normalizedCategory)
                || product.getCategory().getSubcategory().equalsIgnoreCase(normalizedCategory);
    }

    private boolean matchesBrand(Product product, String brand) {
        return product.getBrand() != null && product.getBrand().getName().equalsIgnoreCase(brand.trim());
    }

    private boolean hasPriceAtOrAbove(Product product, BigDecimal minPrice) {
        return product.getSkus().stream()
                .flatMap(sku -> sku.getInventories().stream())
                .anyMatch(inventory -> inventory.getSellingPrice() >= minPrice.doubleValue());
    }

    private boolean hasPriceAtOrBelow(Product product, BigDecimal maxPrice) {
        return product.getSkus().stream()
                .flatMap(sku -> sku.getInventories().stream())
                .anyMatch(inventory -> inventory.getSellingPrice() <= maxPrice.doubleValue());
    }

    private double minPriceFor(Product product) {
        return product.getSkus().stream()
                .flatMap(sku -> sku.getInventories().stream())
                .mapToDouble(Inventory::getSellingPrice)
                .min()
                .orElse(Double.MAX_VALUE);
    }

    @Transactional
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO getProductBySlug(String slug) {
        String normalized = slug == null ? "" : slug.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Product slug is required");
        }

        String expectedSlug = normalized.toLowerCase(Locale.ROOT);
        return productRepository.findAll().stream()
                .filter(product -> product != null)
                .filter(product -> product.isApproved())
                .filter(product -> CategoryDTO.slugify(product.getName()).equals(expectedSlug)
                        || product.getName().equalsIgnoreCase(normalized))
                .findFirst()
                .map(ProductDTO::new)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public ProductDTO getProductByIdOrSlug(String productIdentifier) {
        if (productIdentifier == null || productIdentifier.isBlank()) {
            throw new IllegalArgumentException("Product identifier is required");
        }

        if (productIdentifier.matches("\\d+")) {
            return getProductById(Long.parseLong(productIdentifier));
        }
        return getProductBySlug(productIdentifier);
    }

    public List<ProductDTO> searchProducts(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isBlank()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(normalized).stream()
                .filter(Product::isApproved)
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    public java.util.Map<String, Object> getHomepage() {
        java.util.Map<String, Object> homepage = new java.util.HashMap<>();
        homepage.put("featuredProducts", getProducts(null, null, null, null, true, "featured"));
        homepage.put("categories", getCategories());
        return homepage;
    }

    public List<ProductDTO> getRelatedProducts(Long productId) {
        Product current = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (current.getCategory() == null) {
            return List.of();
        }

        return productRepository.findAll().stream()
                .filter(product -> product != null && !product.getProductId().equals(productId))
                .filter(product -> product.isApproved())
                .filter(product -> product.getCategory() != null)
                .filter(product -> product.getCategory().getCategory().equalsIgnoreCase(current.getCategory().getCategory())
                        || product.getCategory().getSubcategory().equalsIgnoreCase(current.getCategory().getSubcategory()))
                .limit(4)
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    public List<Review> getProductReviews(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return reviewRepository.findByProductId(productId).stream()
                .filter(review -> review != null && review.getRating() != null)
                .collect(Collectors.toList());
    }

    public List<String> getSearchSuggestions(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.length() < 2) {
            return List.of();
        }

        return productRepository.findByNameContaining(normalized).stream()
                .filter(Product::isApproved)
                .map(Product::getName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .limit(10)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductDetails(Long productId) {
        return getProductById(productId);
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        // Find all subcategories under the given category
        List<Category> subcategories = categoryRepository.findByCategory(category);

        // Collect product IDs associated with those subcategories
        List<String> subcategoryNames = subcategories.stream()
                .map(Category::getSubcategory)
                .collect(Collectors.toList());

        // Find all products associated with the found subcategories
        return productRepository.findByCategory_SubcategoryIn(subcategoryNames).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }


    public List<ProductDTO> getProductsBySubcategory(String subcategory) {
        return productRepository.findByCategory_Subcategory(subcategory).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

//    public List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice) {
//        return productRepository.findBySellingPriceBetween(minPrice, maxPrice).stream()
//                .map(ProductDTO::new)
//                .collect(Collectors.toList());
//    }

//    public List<ProductDTO> getProductsOnSale() {
//        return productRepository.findByOnSaleTrue().stream()
//                .map(ProductDTO::new)
//                .collect(Collectors.toList());
//    }

    // Cart Operations (Updated to handle Inventory and SKU correctly)
    @Transactional
    public CartDTO getCart(String userId) {
        Cart cart = cartRepository.findByUserEmail(userId);
        return new CartDTO(cart, userId);
    }

    @Transactional
    public void addProductToCart(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > 10) {
            throw new IllegalArgumentException("Quantity must be between 1 and 10");
        }
        String userId = authenticationHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserEmail(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for the user");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        if (inventory.getSku() == null || !inventory.getSku().isActive()
                || inventory.getSku().getProduct() == null || !inventory.getSku().getProduct().isApproved()) {
            throw new IllegalArgumentException("This variant is not available for purchase");
        }

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getInventory() != null
                        && item.getInventory().getInventoryId().equals(inventoryId))
                .findFirst()
                .orElse(null);
        int newQuantity = quantity + (existingItem == null ? 0 : existingItem.getQuantity());
        if (newQuantity > 10) {
            throw new IllegalArgumentException("Purchase limit is 10 units per variant");
        }
        if (inventory.getStockAvailable() < newQuantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem(cart, inventory, quantity);
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void addVariantToCart(Long variantId, Integer quantity) {
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        List<Inventory> inventories = inventoryRepository.findBySku(sku);
        if (inventories.size() != 1) {
            throw new IllegalStateException("Variant must have exactly one inventory record");
        }
        addProductToCart(inventories.get(0).getInventoryId(), quantity);
    }

    public void removeProductFromCart(Long inventoryId) {
        String userId = authenticationHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserEmail(userId);
        cart.getCartItems().removeIf(item -> item.getInventory().getInventoryId().equals(inventoryId));
        cartRepository.save(cart);
    }

    public void updateCartProductQuantity(Long inventoryId, Integer quantity) {
        String userId = authenticationHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserEmail(userId);
        cart.getCartItems().forEach(item -> {
            if (item.getInventory().getInventoryId().equals(inventoryId)) {
                item.setQuantity(quantity);
            }
        });
        cartRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(Long itemId, Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > 10) {
            throw new IllegalArgumentException("Quantity must be between 1 and 10");
        }
        String userId = authenticationHelper.getCurrentUserId();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (item.getCart() == null || item.getCart().getUser() == null
                || !userId.equalsIgnoreCase(item.getCart().getUser().getEmail())) {
            throw new org.springframework.security.access.AccessDeniedException("Cart item does not belong to the current user");
        }
        Inventory inventory = item.getInventory();
        if (inventory == null || inventory.getSku() == null || !inventory.getSku().isActive()
                || inventory.getSku().getProduct() == null || !inventory.getSku().getProduct().isApproved()) {
            throw new IllegalArgumentException("This variant is not available for purchase");
        }
        if (inventory.getStockAvailable() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void deleteCartItem(Long itemId) {
        String userId = authenticationHelper.getCurrentUserId();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (item.getCart() == null || item.getCart().getUser() == null
                || !userId.equalsIgnoreCase(item.getCart().getUser().getEmail())) {
            throw new org.springframework.security.access.AccessDeniedException("Cart item does not belong to the current user");
        }
        cartItemRepository.delete(item);
    }

    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserEmail(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCurrentCart() {
        String userId = authenticationHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserEmail(userId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return;
        }
        cartItemRepository.deleteAll(new java.util.ArrayList<>(cart.getCartItems()));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    // Checkout process
    @Transactional
    public CheckoutQuoteDTO createCheckoutQuote(CheckoutQuoteRequestDTO request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        String shippingMethod = request.getShippingMethod() == null ? "STANDARD" : request.getShippingMethod().trim().toUpperCase(Locale.ROOT);
        if (!"STANDARD".equals(shippingMethod) && !"EXPRESS".equals(shippingMethod)) {
            throw new IllegalArgumentException("Unsupported shipping method");
        }

        double subtotal = 0.0;
        for (CartItemRequestDTO item : request.getItems()) {
            if (item == null || item.getInventoryId() == null) {
                continue;
            }
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            if (inventory.getSku() == null || !inventory.getSku().isActive()) {
                throw new IllegalArgumentException("This variant is not available for purchase");
            }
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            if (quantity < 1 || quantity > 10) {
                throw new IllegalArgumentException("Quantity must be between 1 and 10");
            }
            subtotal += inventory.getSellingPrice() * quantity;
        }

        double discount = 0.0;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            String coupon = request.getCouponCode().trim().toUpperCase(Locale.ROOT);
            if ("SAVE10".equals(coupon)) {
                discount = subtotal * 0.10;
            }
        }

        double shippingFee = "EXPRESS".equals(shippingMethod) ? 12.50 : 5.00;
        double tax = (subtotal - discount) * 0.08;
        double total = subtotal - discount + shippingFee + tax;
        CheckoutQuoteDTO quote = new CheckoutQuoteDTO();
        quote.setSubtotal(subtotal);
        quote.setDiscount(discount);
        quote.setShippingFee(shippingFee);
        quote.setTax(tax);
        quote.setTotal(total);
        quote.setShippingMethod(shippingMethod);
        quote.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        quote.setFreeShipping(subtotal >= 100.0);
        if (quote.isFreeShipping()) {
            quote.setShippingFee(0.0);
            quote.setTotal(subtotal - discount + tax);
        }
        quote.getWarnings().add("Quote is valid for 15 minutes");
        return quote;
    }

    public OrderDTO checkout() {
        String userId = authenticationHelper.getCurrentUserId();
        // Step 1: Retrieve the user's cart
        Cart cart = cartRepository.findByUserEmail(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Please add items to the cart before checking out.");
        }

        // Step 2: Place the order based on the cart contents
        List<Long> inventoryIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            inventoryIds.add(item.getInventory().getInventoryId());
            quantities.add(item.getQuantity());
        }

        Order order = placeOrder(inventoryIds, quantities);

        // Step 3: Clear the user's cart after placing the order
        cartRepository.deleteById(cart.getCartId());

        // Step 4: Return the order details as a DTO (could include more details as per your design)
        OrderDTO orderDTO = new OrderDTO(order);
        // Populate orderDTO with order details (e.g., order ID, status, etc.)
        // For now, return a basic message (you can enhance this further)
        return orderDTO;
    }

    // Place order logic
    public Order placeOrder(List<Long> inventoryIds, List<Integer> quantities) {
        // Create and save order logic based on inventoryId
        if (inventoryIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Inventory IDs and quantities size must match");
        }

        // Retrieve inventories from database
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryIds);
        if (inventories.size() != inventoryIds.size()) {
            throw new RuntimeException("One or more inventories not found");
        }

        // Retrieve user details
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + userId)
        );

        // Create a new order
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("NEW");
        order.setUser(user);

        // Set order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < inventoryIds.size(); i++) {
            Inventory inventory = inventories.get(i);
            Integer quantity = quantities.get(i);

            // Ensure enough stock exists
            if (inventory.getStockAvailable() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + inventory.getSku().getSkuCode());
            }

            // Create and add order items
            OrderItem orderItem = new OrderItem(inventory.getSku(), quantity, inventory.getSellingPrice());
            orderItems.add(orderItem);

            // Decrease stock in inventory
            inventory.setStockAvailable(inventory.getStockAvailable() - quantity);
            inventoryRepository.save(inventory);
        }

        // Set order items to the order
        order.setOrderItems(orderItems);

        List<Address> addresses = addressRepository.findByUser_Email(userId);
        // Set shipping address (you can extend the logic to handle the user's shipping address)
        // For simplicity, let's assume the shipping address is set via the user object
        order.setShippingAddress(addresses.get(0));

        // Save the order to the database
        orderRepository.save(order);
        return order;
    }

    public OrderDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderDTO(order);
    }

    public List<OrderDTO> getUserOrders(String userId) {
        return orderRepository.findByUser_email(userId).stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("Cancelled");
        orderRepository.save(order);
    }

    public void returnProduct(Long skuId, Long orderId) {
        // Implement return logic based on inventoryId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Optional<OrderItem> item = order.getOrderItems().stream()
                .filter(cartItem -> cartItem.getSku().getSkuId().equals(skuId))
                .findFirst();

        if (item.isPresent()) {
            // Logic to process return (update order status or create a return record)
            item.get().setReturned(true);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product not found in the order");
        }
    }

    @Transactional
    public String mergeGuestCart(String guestCartToken, List<CartItemRequestDTO> guestItems) {
        if (guestCartToken == null || guestCartToken.isBlank()) {
            throw new IllegalArgumentException("Guest cart token is required");
        }
        if (processedGuestCartTokens.contains(guestCartToken)) {
            throw new IllegalStateException("Guest cart has already been merged");
        }

        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userId));
        Cart cart = cartRepository.findByUserEmail(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }

        if (guestItems == null || guestItems.isEmpty()) {
            processedGuestCartTokens.add(guestCartToken);
            return "guest-" + UUID.randomUUID();
        }

        for (CartItemRequestDTO guestItem : guestItems) {
            if (guestItem == null) {
                continue;
            }

            Long inventoryId = guestItem.getInventoryId();
            if (inventoryId == null && guestItem.getVariantId() != null) {
                SKU sku = skuRepository.findById(guestItem.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Variant not found"));
                List<Inventory> variants = inventoryRepository.findBySku(sku);
                if (variants.isEmpty()) {
                    throw new RuntimeException("Variant has no inventory record");
                }
                inventoryId = variants.get(0).getInventoryId();
            }
            if (inventoryId == null) {
                throw new IllegalArgumentException("Inventory or variant is required");
            }

            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            if (inventory.getSku() == null || !inventory.getSku().isActive()
                    || inventory.getSku().getProduct() == null || !inventory.getSku().getProduct().isApproved()) {
                throw new IllegalArgumentException("This variant is not available for purchase");
            }

            int requestedQuantity = guestItem.getQuantity() == null ? 1 : guestItem.getQuantity();
            if (requestedQuantity < 1 || requestedQuantity > 10) {
                throw new IllegalArgumentException("Quantity must be between 1 and 10");
            }

            final Long targetInventoryId = inventoryId;
            CartItem existingItem = cart.getCartItems().stream()
                    .filter(item -> item.getInventory() != null && item.getInventory().getInventoryId().equals(targetInventoryId))
                    .findFirst()
                    .orElse(null);

            int mergedQuantity = (existingItem == null ? 0 : existingItem.getQuantity()) + requestedQuantity;
            int cappedQuantity = Math.min(10, mergedQuantity);
            if (inventory.getStockAvailable() < cappedQuantity) {
                throw new IllegalArgumentException("Not enough stock available");
            }

            if (existingItem != null) {
                existingItem.setQuantity(cappedQuantity);
                cartItemRepository.save(existingItem);
            } else {
                CartItem newItem = new CartItem(cart, inventory, cappedQuantity);
                cart.getCartItems().add(newItem);
                cartItemRepository.save(newItem);
            }
        }

        processedGuestCartTokens.add(guestCartToken);
        return "guest-" + UUID.randomUUID();
    }

    public List<InventoryDTO> getWishlist() {
        String userId = authenticationHelper.getCurrentUserId();
        WishList wishlist = wishListRepository.findByUserEmail(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userId));
                    WishList created = new WishList();
                    created.setUser(user);
                    created.setInventories(new ArrayList<>());
                    return wishListRepository.save(created);
                });

        if (wishlist.getInventories() == null) {
            return List.of();
        }

        return wishlist.getInventories().stream()
                .filter(item -> item != null)
                .map(InventoryDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addProductToWishlist(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        WishList wishlist = getWishList();
        if (wishlist.getInventories() == null) {
            wishlist.setInventories(new ArrayList<>());
        }
        if (wishlist.getInventories().stream().noneMatch(item -> item != null && item.getInventoryId().equals(inventoryId))) {
            inventory.setWishlist(wishlist);
            wishlist.getInventories().add(inventory);
            wishListRepository.save(wishlist);
        }
    }

    @Transactional
    public void addVariantToWishlist(Long variantId) {
        SKU sku = skuRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        List<Inventory> inventories = inventoryRepository.findBySku(sku);
        if (inventories.isEmpty()) {
            throw new RuntimeException("Variant has no inventory record");
        }
        addProductToWishlist(inventories.get(0).getInventoryId());
    }

    @Transactional
    public void removeWishlistItem(Long variantId) {
        WishList wishlist = getWishList();
        if (wishlist.getInventories() == null || wishlist.getInventories().isEmpty()) {
            return;
        }

        List<Inventory> remaining = wishlist.getInventories().stream()
                .filter(item -> item != null && (item.getSku() == null || !item.getSku().getSkuId().equals(variantId)))
                .collect(Collectors.toList());

        wishlist.getInventories().forEach(item -> {
            if (item != null && item.getSku() != null && item.getSku().getSkuId().equals(variantId)) {
                item.setWishlist(null);
            }
        });
        wishlist.setInventories(remaining);
        wishListRepository.save(wishlist);
    }

    @Transactional
    public void moveWishlistItemToCart(Long variantId, Integer quantity) {
        WishList wishlist = getWishList();
        Inventory inventory = wishlist.getInventories().stream()
                .filter(item -> item != null && item.getSku() != null && item.getSku().getSkuId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        addProductToCart(inventory.getInventoryId(), quantity == null ? 1 : quantity);
        removeWishlistItem(variantId);
    }

    public void addProductToWishList(String userId, Long inventoryId) {
        addProductToWishlist(inventoryId);
    }

    public void removeProductFromWishList(Inventory inventory) {
        if (inventory != null && inventory.getSku() != null) {
            removeWishlistItem(inventory.getSku().getSkuId());
        }
    }

    public boolean isProductInWishList(Long inventoryId) {
        WishList wishlist = getWishList();
        return wishlist.getInventories() != null && wishlist.getInventories().stream()
                .anyMatch(inventory -> inventory != null && inventory.getInventoryId().equals(inventoryId));
    }

    private WishList getWishList() {
        String userId = authenticationHelper.getCurrentUserId();
        return wishListRepository.findByUserEmail(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userId));
                    WishList created = new WishList();
                    created.setUser(user);
                    created.setInventories(new ArrayList<>());
                    return wishListRepository.save(created);
                });
    }

    // Review and Rating Management (No change)
    public void leaveReview(String userId, Long productId, String reviewText, Integer rating) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review(userId, product, reviewText, rating);
        product.getReviews().add(review);
        productRepository.save(product);
    }

    public List<ProductDTO> filterProducts(String category, String keyword) {
        String normalizedCategory = category == null ? "" : category.trim();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (normalizedCategory.isBlank() && normalizedKeyword.isBlank()) {
            return getAllProducts();
        }

        if (normalizedCategory.isBlank()) {
            return searchProducts(normalizedKeyword);
        }

        List<Product> filteredProducts = productRepository.findByCategory_SubcategoryAndNameContaining(normalizedCategory, normalizedKeyword.isBlank() ? "" : normalizedKeyword);
        return filteredProducts.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUser(String userId) {
        return orderRepository.findByUser_email(userId).stream().map(OrderDTO::new).toList();
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));  // Fetch specific order by ID
        return new OrderDTO(order);
    }

    @Transactional
    @Modifying
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));  // Fetch order by ID
        order.setOrderStatus(status);  // Set the new status to the order
        orderRepository.save(order);  // Save the updated order
    }

    public UserDTO getUserProfile() {
        return userService.getAuthenticatedUser();
    }

    public List<AddressDTO> getUserAddresses() {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive() || user.isDeleted() || user.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }
        return addressRepository.findByUser_Email(userId).stream()
                .map(AddressDTO::new)
                .collect(Collectors.toList());
    }

    public List<ShippingMethodDTO> getShippingMethods(String country, String postalCode, Double subtotal) {
        String normalizedCountry = country == null ? "" : country.trim();
        if (normalizedCountry.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }

        double subtotalValue = subtotal == null ? 0.0 : subtotal;
        List<ShippingMethodDTO> methods = new ArrayList<>();

        ShippingMethodDTO standard = new ShippingMethodDTO();
        standard.setCode("STANDARD");
        standard.setLabel("Standard Shipping");
        standard.setEstimate("3-5 business days");
        standard.setFee(subtotalValue >= 100.0 ? 0.0 : 5.0);
        standard.setFreeShippingEligible(subtotalValue >= 100.0);
        methods.add(standard);

        ShippingMethodDTO express = new ShippingMethodDTO();
        express.setCode("EXPRESS");
        express.setLabel("Express Shipping");
        express.setEstimate("1-2 business days");
        express.setFee(subtotalValue >= 150.0 ? 0.0 : 12.5);
        express.setFreeShippingEligible(subtotalValue >= 150.0);
        methods.add(express);

        return methods;
    }

    public CouponValidationDTO validateCoupon(CouponValidationRequestDTO request) {
        CouponValidationDTO response = new CouponValidationDTO();
        if (request == null || request.getCouponCode() == null || request.getCouponCode().isBlank()) {
            response.setValid(false);
            response.setMessage("Coupon code is required.");
            return response;
        }

        String code = request.getCouponCode().trim().toUpperCase(Locale.ROOT);
        double subtotal = request.getSubtotal() == null ? 0.0 : request.getSubtotal();
        response.setCouponCode(code);

        if ("SAVE10".equals(code)) {
            if (subtotal < 10.0) {
                response.setValid(false);
                response.setMessage("Minimum purchase of $10.00 required for SAVE10.");
                return response;
            }
            response.setValid(true);
            response.setDiscountAmount(Math.min(subtotal * 0.10, 100.0));
            response.setMessage("Coupon is valid and has not been consumed.");
            return response;
        }

        response.setValid(false);
        response.setMessage("Coupon code is invalid or not eligible for this cart.");
        return response;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream()
                .sorted((left, right) -> {
                    int categoryCompare = left.getCategory().compareToIgnoreCase(right.getCategory());
                    if (categoryCompare != 0) {
                        return categoryCompare;
                    }
                    return left.getSubcategory().compareToIgnoreCase(right.getSubcategory());
                })
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public List<BrandDTO> getBrands() {
        return brandRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(BrandDTO::new)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Category slug is required");
        }

        String targetSlug = slug.trim().toLowerCase(Locale.ROOT);

        return categoryRepository.findAll().stream()
                .filter(category -> matchesCategorySlug(category, targetSlug))
                .min(Comparator.comparing(category -> category.getCategory().toLowerCase(Locale.ROOT)))
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO(category);
                    dto.setSlug(targetSlug);
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private boolean matchesCategorySlug(Category category, String targetSlug) {
        if (category == null) {
            return false;
        }

        String categorySlug = CategoryDTO.slugify(category.getCategory());
        String subcategorySlug = CategoryDTO.slugify(category.getSubcategory());
        String combinedSlug = CategoryDTO.buildSlug(category.getCategory(), category.getSubcategory());

        return categorySlug.equals(targetSlug)
                || subcategorySlug.equals(targetSlug)
                || combinedSlug.equals(targetSlug);
    }

    public Address createAddress(AddressDTO addressDTO) {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive() || user.isDeleted() || user.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }
        if (addressDTO == null) {
            throw new IllegalArgumentException("Address payload is required.");
        }
        String street = addressDTO.getStreet() == null ? "" : addressDTO.getStreet().trim();
        String city = addressDTO.getCity() == null ? "" : addressDTO.getCity().trim();
        String state = addressDTO.getState() == null ? "" : addressDTO.getState().trim();
        String country = addressDTO.getCountry() == null ? "" : addressDTO.getCountry().trim();
        String postalCode = addressDTO.getPostalCode() == null ? "" : addressDTO.getPostalCode().trim();
        if (street.isBlank() || city.isBlank() || country.isBlank() || postalCode.isBlank()) {
            throw new IllegalArgumentException("Street, city, country and postal code are required.");
        }

        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        address.setUser(user);
        return addressRepository.save(address);
    }

    public Address updateAddress(Long addressId, AddressDTO addressDTO) {
        String userId = authenticationHelper.getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!currentUser.isActive() || currentUser.isDeleted() || currentUser.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }
        if (addressDTO == null) {
            throw new IllegalArgumentException("Address payload is required.");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (address.getUser() == null || !userId.equalsIgnoreCase(address.getUser().getEmail())) {
            throw new RuntimeException("You can only update your own address.");
        }

        if (addressDTO.getStreet() != null) {
            String street = addressDTO.getStreet().trim();
            if (street.isBlank()) throw new IllegalArgumentException("Street cannot be blank.");
            address.setStreet(street);
        }
        if (addressDTO.getCity() != null) {
            String city = addressDTO.getCity().trim();
            if (city.isBlank()) throw new IllegalArgumentException("City cannot be blank.");
            address.setCity(city);
        }
        if (addressDTO.getState() != null) {
            address.setState(addressDTO.getState().trim());
        }
        if (addressDTO.getCountry() != null) {
            String country = addressDTO.getCountry().trim();
            if (country.isBlank()) throw new IllegalArgumentException("Country cannot be blank.");
            address.setCountry(country);
        }
        if (addressDTO.getPostalCode() != null) {
            String postalCode = addressDTO.getPostalCode().trim();
            if (postalCode.isBlank()) throw new IllegalArgumentException("Postal code cannot be blank.");
            address.setPostalCode(postalCode);
        }

        return addressRepository.save(address);
    }

    public String deleteAddress(Long addressId) {
        String userId = authenticationHelper.getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!currentUser.isActive() || currentUser.isDeleted() || currentUser.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (address.getUser() == null || !userId.equalsIgnoreCase(address.getUser().getEmail())) {
            throw new RuntimeException("You can only delete your own address.");
        }

        addressRepository.delete(address);
        return "Address deleted successfully.";
    }

    public String setDefaultAddress(Long addressId) {
        String userId = authenticationHelper.getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!currentUser.isActive() || currentUser.isDeleted() || currentUser.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }

        Address target = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (target.getUser() == null || !userId.equalsIgnoreCase(target.getUser().getEmail())) {
            throw new RuntimeException("You can only set your own address as default.");
        }

        List<Address> userAddresses = addressRepository.findByUser_Email(userId);
        for (Address address : userAddresses) {
            if (address.getAddressId() != null && address.getAddressId().equals(addressId)) {
                address.setDefaultAddress(true);
                addressRepository.save(address);
            } else if (address.isDefaultAddress()) {
                address.setDefaultAddress(false);
                addressRepository.save(address);
            }
        }

        return "Default address set successfully.";
    }

    public void updateUserProfile(UserDTO userDTO) {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive() || user.isDeleted() || user.isAnonymized()) {
            throw new RuntimeException("This account is inactive or has been anonymized.");
        }
        if (userDTO != null && userDTO.getEmail() != null && !userDTO.getEmail().equalsIgnoreCase(userId)) {
            throw new RuntimeException("Email changes require a verification workflow.");
        }
        userService.updateUser(userId, userDTO);
    }

    @Transactional
    public String requestAccountDeactivation() {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isDeleted() || user.isAnonymized()) {
            throw new RuntimeException("This account cannot be deactivated.");
        }
        user.setActive(false);
        user.setDeactivationRequestedAt(LocalDateTime.now());
        userRepository.save(user);
        return "Account deactivation requested successfully. Please sign in again to reactivate your account.";
    }

    @Transactional
    public String requestAccountDeletion() {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isDeleted() || user.isAnonymized()) {
            throw new RuntimeException("This account has already been processed for deletion.");
        }
        user.setDeletionRequested(true);
        user.setDeletionRequestedAt(LocalDateTime.now());
        user.setRetentionDeadline(LocalDateTime.now().plusDays(30));
        userRepository.save(user);
        return "Account deletion requested. Record anonymization will occur after the retention window.";
    }

    @Transactional
    public String anonymizeUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isDeletionRequested() || user.getRetentionDeadline() == null || user.getRetentionDeadline().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("User is not eligible for anonymization yet.");
        }
        user.setActive(false);
        user.setDeleted(true);
        user.setAnonymized(true);
        user.setAnonymizedAt(LocalDateTime.now());
        user.setFirstName("ANONYMIZED");
        user.setLastName("USER");
        user.setDob(null);
        user.setPassword("ANONYMIZED");
        user.setEmail("deleted-" + System.currentTimeMillis() + "@anon.local");
        user.setDeletionRequested(false);
        user.setRetentionDeadline(null);
        userRepository.save(user);
        return "User account anonymized successfully.";
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void anonymizeExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<User> usersToAnonymize = userRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.isDeletionRequested())
                        && user.getRetentionDeadline() != null
                        && user.getRetentionDeadline().isBefore(now))
                .collect(Collectors.toList());
        for (User user : usersToAnonymize) {
            anonymizeUser(user.getEmail());
        }
    }

    public OrderDTO checkout(String userId) {
        return checkout();
    }

    public OrderDTO processGuestCheckout(GuestOrderDTO guestOrderDTO) {
        throw new UnsupportedOperationException("Guest checkout is not yet implemented");
    }

    public void processPayment(Long orderId, PaymentDTO paymentDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("PAID");
        orderRepository.save(order);
    }

    public List<OrderDTO> getOrderHistory(String userId) {
        return getOrdersByUser(userId);
    }
}
