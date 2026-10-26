package com.example.onlineshop.config;

import com.example.onlineshop.cart.entity.Cart;
import com.example.onlineshop.cart.entity.CartItem;
import com.example.onlineshop.product.entity.Category;
import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.user.entity.Permission;
import com.example.onlineshop.product.entity.Product;
import com.example.onlineshop.user.entity.Role;
import com.example.onlineshop.product.entity.SKU;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.cart.repository.CartItemRepository;
import com.example.onlineshop.cart.repository.CartRepository;
import com.example.onlineshop.product.repository.CategoryRepository;
import com.example.onlineshop.product.repository.InventoryRepository;
import com.example.onlineshop.user.repository.PermissionRepository;
import com.example.onlineshop.product.repository.ProductRepository;
import com.example.onlineshop.user.repository.RoleRepository;
import com.example.onlineshop.product.repository.SKURepository;
import com.example.onlineshop.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Component
@Profile("!test")
public class StandardDataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SKURepository skuRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final boolean enabled;
    private final String adminPassword;
    private final String managerPassword;
    private final String buyerPassword;

    public StandardDataInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            SKURepository skuRepository,
            InventoryRepository inventoryRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate,
            @Value("${app.seed.enabled:true}") boolean enabled,
            @Value("${app.seed.admin-password:Admin123!}") String adminPassword,
            @Value("${app.seed.manager-password:Manager123!}") String managerPassword,
            @Value("${app.seed.buyer-password:Buyer123!}") String buyerPassword) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.enabled = enabled;
        this.adminPassword = adminPassword;
        this.managerPassword = managerPassword;
        this.buyerPassword = buyerPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        repairLegacyProductForeignKey();
        Map<String, Permission> permissions = seedPermissions();

        Role administrator = seedRole("ADMINISTRATOR", permissions.values().stream().toList());
        Role adminAlias = seedRole("ADMIN", permissions.values().stream().toList());

        Role staff = seedRole("STAFF", List.of(
                permissions.get("product:create"), permissions.get("product:update"),
                permissions.get("product:delete"), permissions.get("product:stock"),
                permissions.get("product:approve"), permissions.get("order:read"),
                permissions.get("order:update"), permissions.get("user:read")));
        Role managerAlias = seedRole("MANAGER", staff.getPermissions());

        Role customer = seedRole("CUSTOMER", List.of(
                permissions.get("user:read"), permissions.get("user:update")));
        Role buyerAlias = seedRole("BUYER", List.of());

        User adminUser = seedUser("admin@example.com", "Admin", "User", adminPassword, List.of(administrator, adminAlias));
        User managerUser = seedUser("manager@example.com", "Manager", "User", managerPassword, List.of(staff, managerAlias));
        User buyerUser = seedUser("buyer@example.com", "Buyer", "User", buyerPassword, List.of(customer, buyerAlias));

        seedCatalog();
        seedCart(adminUser, null, 0);
        seedCart(managerUser, null, 0);
        Inventory sampleInventory = firstInventory();
        seedCart(buyerUser, sampleInventory, 1);
    }

        private void repairLegacyProductForeignKey() {
        Integer legacyConstraintCount = jdbcTemplate.queryForObject(
            "select count(*) from information_schema.table_constraints " +
                "where constraint_schema = database() and table_name = 'products' " +
                "and constraint_name = 'FKtng6hvelpjyy7el0f5eq93nq4'",
            Integer.class);
        if (legacyConstraintCount != null && legacyConstraintCount > 0) {
            jdbcTemplate.execute("alter table products drop foreign key FKtng6hvelpjyy7el0f5eq93nq4");
        }

        Integer compositeConstraintCount = jdbcTemplate.queryForObject(
            "select count(*) from information_schema.table_constraints " +
                "where constraint_schema = database() and table_name = 'products' " +
                "and constraint_name = 'fk_products_category'",
            Integer.class);
        if (compositeConstraintCount != null && compositeConstraintCount == 0) {
            jdbcTemplate.execute("alter table products add constraint fk_products_category " +
                "foreign key (category, subcategory) references categories (category, subcategory)");
        }
        }

    private Map<String, Permission> seedPermissions() {
        List<String> names = List.of(
                "product:create", "product:update", "product:delete", "product:stock", "product:approve",
                "order:read", "order:update", "user:create", "user:read", "user:update", "user:delete",
                "role:create", "role:read", "role:update", "role:delete", "role:assign",
                "settings:read", "settings:update", "dashboard:read", "audit:read",
                "profile:update", "account:deactivate", "account:delete", "account:anonymize");
        return names.stream().collect(java.util.stream.Collectors.toMap(name -> name, this::permission));
    }

    private Permission permission(String name) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            return permissionRepository.save(permission);
        });
    }

    private Role seedRole(String name, List<Permission> permissions) {
        Role role = roleRepository.findByName(name).orElseGet(Role::new);
        role.setName(name);
        role.setPermissions(new ArrayList<>(permissions));
        return roleRepository.save(role);
    }

    private User seedUser(String email, String firstName, String lastName, String password, List<Role> roles) {
        User user = userRepository.findById(email).orElseGet(User::new);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDob("1990-01-01");
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        user.setRoles(new ArrayList<>(roles));
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return userRepository.save(user);
    }

    private void seedCatalog() {
        seedProduct("Linen overshirt", "A relaxed layer cut from breathable European linen.", "Apparel", "Outerwear", "LO-M-INK", "M", "Ink", 128, 8);
        seedProduct("Form ceramic set", "Hand-thrown vessels that bring a quiet rhythm to the table.", "Home", "Tabletop", "FC-SET-CLAY", "Set of 3", "Clay", 86, 12);
        seedProduct("Daily tote", "A durable canvas carryall for everyday errands and escapes.", "Accessories", "Bags", "DT-ONE-NAT", "One size", "Natural", 64, 20);
    }

    private Product seedProduct(String name, String description, String categoryName, String subcategoryName, String skuCode, String size, String color, double price, int stock) {
        Product product = productRepository.findAll().stream().filter(item -> name.equals(item.getName())).findFirst().orElseGet(Product::new);
        Category category = categoryRepository.findByCategoryAndSubcategory(categoryName, subcategoryName).orElseGet(() -> {
            Category value = new Category();
            value.setCategory(categoryName);
            value.setSubcategory(subcategoryName);
            return categoryRepository.save(value);
        });
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setApproved(true);
        product.setAvailable(stock > 0);
        Product savedProduct = productRepository.save(product);
        SKU sku = skuRepository.findByProduct(savedProduct).stream().findFirst().orElseGet(SKU::new);
        sku.setProduct(savedProduct);
        sku.setSkuCode(skuCode);
        sku.setSize(size);
        sku.setColor(color);
        SKU savedSku = skuRepository.save(sku);
        Inventory inventory = inventoryRepository.findBySku(savedSku).stream().findFirst().orElseGet(Inventory::new);
        inventory.setSku(savedSku);
        inventory.setCostPrice(price * 0.55);
        inventory.setSellingPrice(price);
        inventory.setStockAvailable(stock);
        inventoryRepository.save(inventory);
        return savedProduct;
    }

    private Inventory firstInventory() {
        return inventoryRepository.findAll().stream().findFirst().orElse(null);
    }

    private void seedCart(User user, Inventory inventory, int quantity) {
        Cart cart = cartRepository.findByUserEmail(user.getEmail());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        if (inventory != null && cartItemRepository.findByCart(cart).stream().noneMatch(item -> item.getInventory().getInventoryId().equals(inventory.getInventoryId()))) {
            cartItemRepository.save(new CartItem(cart, inventory, quantity));
        }
    }
}
