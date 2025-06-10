package com.yeshwanth.sbr.service;

import com.yeshwanth.sbr.dto.CartSummary;

public interface CartService {

    public CartSummary getCartSummaryByUserId(Long userId);
    public CartSummary addToCart(Long userId, String productCode, int quantity);

    public CartSummary removeFromCart(String productCode, Long userId);
    public void clearCart(Long userId);
}
