package com.yeshwanth.sbr.controller;

import com.yeshwanth.sbr.dto.CartSummary;
import com.yeshwanth.sbr.exception.ProductQuntityException;
import com.yeshwanth.sbr.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartSummary> getCartSummary(@PathVariable Long userId) {
        log.debug("Fetching cart summary for user: {}", userId);
        CartSummary cartSummary = cartService.getCartSummaryByUserId(userId);
        return ResponseEntity.ok(cartSummary);
    }

    @PostMapping("/{userId}/products")
    public ResponseEntity<CartSummary> addToCart(
            @PathVariable Long userId,
            @RequestParam String productCode,
            @RequestParam int quantity) {
        if (quantity <= 0) {
            log.warn("Invalid quantity: {} for product: {} for user: {}", quantity, productCode, userId);
            throw new ProductQuntityException("Quantity must be greater than zero");
        }

        log.debug("Adding product {} with quantity {} to cart for user: {}", productCode, quantity, userId);
        CartSummary cartSummary = cartService.addToCart(userId, productCode, quantity);
        return ResponseEntity.ok(cartSummary);
    }

    @DeleteMapping("/{userId}/products/{productCode}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long userId,
            @PathVariable String productCode) {
        log.debug("Removing product {} from cart for user: {}", productCode, userId);
        CartSummary cartSummary = cartService.removeFromCart(productCode, userId);
         if(cartSummary == null) {
             return ResponseEntity.ok("CART IS EMPTY");
         }
        return ResponseEntity.ok(cartSummary);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        log.debug("Clearing cart for user: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart has been cleared");
    }
}
