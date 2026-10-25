package com.example.onlineshop.cart.controller;

import com.example.onlineshop.cart.dto.CartDTO;
import com.example.onlineshop.cart.dto.CartItemRequestDTO;
import com.example.onlineshop.common.service.BuyerService;
import com.example.onlineshop.common.security.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Cart", description = "Shopping cart operations")
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Operation(summary = "Get current user's cart", description = "Retrieve the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Cart not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public CartDTO getCart() {
        return buyerService.getCart(authenticationHelper.getCurrentUserId());
    }

    @Operation(summary = "Add item to cart", description = "Add a product to the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to cart successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/items")
    public void addItem(@RequestBody CartItemRequestDTO request) {
        if (request.getVariantId() != null) {
            buyerService.addVariantToCart(request.getVariantId(), request.getQuantity());
        } else {
            buyerService.addProductToCart(request.getInventoryId(), request.getQuantity());
        }
    }

    @Operation(summary = "Remove item from cart", description = "Remove a product from the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed from cart successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Cart or item not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/items/by-inventory/{inventoryId}")
    public void removeItem(@PathVariable Long inventoryId) {
        buyerService.removeProductFromCart(inventoryId);
    }

    @Operation(summary = "Delete an owned cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart item deleted."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "403", description = "Cart item belongs to another user."),
            @ApiResponse(responseCode = "404", description = "Cart item not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/items/{itemId}")
    public org.springframework.http.ResponseEntity<Void> deleteCartItem(@PathVariable Long itemId) {
        buyerService.deleteCartItem(itemId);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update item quantity in cart", description = "Update the quantity of a product in the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item quantity updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid quantity."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/items/by-inventory/{inventoryId}")
    public void updateItemQuantity(
            @PathVariable Long inventoryId,
            @RequestBody CartItemRequestDTO request) {
        buyerService.updateCartProductQuantity(inventoryId, request.getQuantity());
    }

    @Operation(summary = "Update an owned cart item quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart item updated."),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "403", description = "Cart item belongs to another user."),
            @ApiResponse(responseCode = "404", description = "Cart item not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/items/{itemId}")
    public org.springframework.http.ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody CartItemRequestDTO request) {
        buyerService.updateCartItemQuantity(itemId, request.getQuantity());
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear cart", description = "Remove all products from the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Cart not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/legacy")
    public void clearCart() {
        buyerService.clearCart(authenticationHelper.getCurrentUserId());
    }

    @Operation(summary = "Clear the current user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCurrentCart() {
        buyerService.clearCurrentCart();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Merge a guest cart into the current user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest cart merged."),
            @ApiResponse(responseCode = "400", description = "Invalid request or quantity."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "409", description = "Guest cart replay detected.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/merge")
    public ResponseEntity<Map<String, String>> mergeGuestCart(@RequestBody(required = false) Map<String, Object> payload) {
        String guestCartToken = payload == null ? null : String.valueOf(payload.getOrDefault("guestCartToken", payload.getOrDefault("token", "")));
        List<Map<String, Object>> guestItems = payload == null ? List.of() : (List<Map<String, Object>>) payload.getOrDefault("items", List.of());
        List<CartItemRequestDTO> normalizedItems = guestItems.stream().map(item -> {
            CartItemRequestDTO dto = new CartItemRequestDTO();
            Object inventoryId = item.get("inventoryId");
            Object variantId = item.get("variantId");
            Object quantity = item.get("quantity");
            if (inventoryId != null) {
                dto.setInventoryId(Long.valueOf(String.valueOf(inventoryId)));
            }
            if (variantId != null) {
                dto.setVariantId(Long.valueOf(String.valueOf(variantId)));
            }
            if (quantity != null) {
                dto.setQuantity(Integer.valueOf(String.valueOf(quantity)));
            }
            return dto;
        }).toList();

        String rotatedToken = buyerService.mergeGuestCart(guestCartToken, normalizedItems);
        Map<String, String> response = new HashMap<>();
        response.put("guestCartToken", rotatedToken);
        return ResponseEntity.ok(response);
    }
}
