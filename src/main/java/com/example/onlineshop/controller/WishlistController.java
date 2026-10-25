package com.example.onlineshop.controller;

import com.example.onlineshop.dto.CartItemRequestDTO;
import com.example.onlineshop.dto.InventoryDTO;
import com.example.onlineshop.service.BuyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Wishlist", description = "User wishlist operations")
@RestController
@RequestMapping("/api/v1")
public class WishlistController {

    @Autowired
    private BuyerService buyerService;

    @Operation(summary = "List the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wishlist returned."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/wishlist")
    public List<InventoryDTO> getWishlist() {
        return buyerService.getWishlist();
    }

    @Operation(summary = "Add an inventory or variant to the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Wishlist item added."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/wishlist/items")
    public ResponseEntity<Void> addWishlistItem(@RequestBody CartItemRequestDTO request) {
        if (request == null) {
            return ResponseEntity.noContent().build();
        }
        if (request.getVariantId() != null) {
            buyerService.addVariantToWishlist(request.getVariantId());
        } else if (request.getInventoryId() != null) {
            buyerService.addProductToWishlist(request.getInventoryId());
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove a variant from the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Wishlist item removed."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/wishlist/items/{variantId}")
    public ResponseEntity<Void> removeWishlistItem(@PathVariable Long variantId) {
        buyerService.removeWishlistItem(variantId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Move a wishlist item into the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item moved to cart."),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/wishlist/items/{variantId}/move-to-cart")
    public ResponseEntity<Void> moveWishlistItemToCart(@PathVariable Long variantId,
                                                      @RequestBody(required = false) CartItemRequestDTO request) {
        Integer quantity = request == null ? 1 : request.getQuantity();
        buyerService.moveWishlistItemToCart(variantId, quantity == null ? 1 : quantity);
        return ResponseEntity.noContent().build();
    }
}
