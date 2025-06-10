package com.yeshwanth.sbr.service;

import com.yeshwanth.sbr.dto.CartEntryDto;
import com.yeshwanth.sbr.dto.CartEntryDtoMapper;
import com.yeshwanth.sbr.dto.CartSummary;
import com.yeshwanth.sbr.dto.UserDtoMapper;
import com.yeshwanth.sbr.exception.CartServiceException;
import com.yeshwanth.sbr.model.Cart;
import com.yeshwanth.sbr.model.CartEntry;
import com.yeshwanth.sbr.model.Product;
import com.yeshwanth.sbr.model.User;
import com.yeshwanth.sbr.repository.CartRepository;
import com.yeshwanth.sbr.repository.ProductRepository;
import com.yeshwanth.sbr.repository.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;



     @Override
     @Transactional
     @CachePut(value = "cartSummary", key = "#userId")
     public CartSummary addToCart(Long userId, String productCode, int quantity) {

        log.info("Adding product {} to cart for userId: {}", productCode, userId);

         Product product =productRepository.findByCode(productCode);

         User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartServiceException("User not found"));

         Optional<Cart> userCart = cartRepository.findByUser(user);
         if(userCart.isEmpty()){

            log.info("new cart creating for userId: {}", userId);
            Cart newCart = createNewCart(user, product, quantity);
            cartRepository.save(newCart);
             log.info("New cart created for userId: {} and cartId: {}", userId, newCart.getId());

            Collection<CartEntry> cartEntries = newCart.getCartEntries();

             return getCartSummary(cartEntries, newCart, user);
         }
        else{
             log.info("existing cart found for userId: {} and cartId: {}", userId, userCart.get().getId());
             Cart existingCart = cartRepository.findByUser(user).orElseThrow(() -> new CartServiceException("Cart not found"));

            // Check if the product already exists in the cart

            boolean isProductExist= false;
            Collection<CartEntry> cartEntries = existingCart.getCartEntries();
            for(CartEntry cartEntry :cartEntries){
                if(cartEntry.getProduct()==product)
                {
                    cartEntry.setQuantity(quantity);
                    cartEntry.setPrice(quantity * product.getPrice());
                    isProductExist = true;
                    log.info("Product {} already exists in cart, updated quantity to {}", productCode, cartEntry.getQuantity());
                }
            }
            if(!isProductExist){
                CartEntry cartEntry = new CartEntry();
                cartEntry.setProduct(product);
                cartEntry.setQuantity(quantity);
                cartEntry.setPrice(product.getPrice() * quantity);
                cartEntries.add(cartEntry);
            }
            existingCart.setCartEntries(cartEntries);
             existingCart.setTotal(calculateTotal(cartEntries).setScale(2, RoundingMode.HALF_UP));
             for (CartEntry entry : cartEntries) {
                 entry.setCart(existingCart);
             }
             cartRepository.save(existingCart);
             log.info("Cart updated for userId: {} and cartId: {}", userId, existingCart.getId());
             return getCartSummary(cartEntries, existingCart, user);

         }
    }

    private CartSummary getCartSummary(Collection<CartEntry> cartEntries, Cart cart, User user) {
        CartSummary cartSummary = new CartSummary();
        cartSummary.setCartId(cart.getId());
        cartSummary.setUser(UserDtoMapper.mapToUserDto(user));
        Collection<CartEntryDto> cartEntryDtos = CartEntryDtoMapper.mapToCartEntryDto(cartEntries);
        cartSummary.setEntrys(cartEntryDtos);
        cartSummary.setTotalQuantity(cartEntries.size());
        cartSummary.setTotalPrice(cart.getTotal());

        return cartSummary;
    }

    @Override
    @Transactional
    @CacheEvict(value = "cartSummary", key = "#userId")
    public CartSummary removeFromCart(String productCode, Long userId) {

        log.info("Removing product {} from cart for userId: {}", productCode, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartServiceException("User not found"));
        Cart existingcart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartServiceException("Cart not found"));
        Product product = productRepository.findByCode(productCode);

        existingcart.getCartEntries().removeIf(cartEntry -> cartEntry.getProduct() == product);
        BigDecimal total = calculateTotal(existingcart.getCartEntries());
        existingcart.setTotal(total.setScale(2, RoundingMode.HALF_UP));

        if(existingcart.getCartEntries().isEmpty()) {
            log.info("Cart is empty after removing product {}, deleting cart for userId: {}", productCode, userId);
            cartRepository.delete(existingcart);

            return null; //
        }


        cartRepository.save(existingcart);

        log.info("Product {} removed from cart for userId: {}", productCode, userId);
        // Create CartSummary
        CartSummary cartSummary = new CartSummary();
        cartSummary.setCartId(existingcart.getId());
        cartSummary.setUser(UserDtoMapper.mapToUserDto(user));
        Collection<CartEntryDto> cartEntryDtos= CartEntryDtoMapper.mapToCartEntryDto(existingcart.getCartEntries());
        cartSummary.setEntrys(cartEntryDtos);
        cartSummary.setTotalQuantity(existingcart.getCartEntries().size());

        cartSummary.setTotalPrice(existingcart.getTotal());

        return cartSummary;

    }

    @Override
    @Transactional
    @CacheEvict(value = "cartSummary", key = "#userId")
    public void clearCart(Long userId) {
        log.info("Clearing cart for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartServiceException("User not found"));

        // Check if the cart exists for the user
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartServiceException("Cart not found"));

        log.info("Cart found for userId: {}, clearing cart entries", userId);

        cartRepository.delete(cart);

        log.info("Cart cleared for userId: {}", userId);
        }

    private Cart createNewCart(User user, Product product, int quantity) {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProduct(product);
        cartEntry.setQuantity(quantity);
        cartEntry.setPrice(cartEntry.getQuantity()*product.getPrice());

        // Set the total price of the cart
        Collection<CartEntry> cartEntryCollection = new ArrayList<>();
        cartEntryCollection.add(cartEntry);

        Cart newCart = new Cart();
        newCart.setCartEntries(cartEntryCollection);
        newCart.setUser(user);
        newCart.setTotal(BigDecimal.valueOf(cartEntry.getPrice()).setScale(2, RoundingMode.HALF_UP));

        cartEntry.setCart(newCart);

        return newCart;
    }


    private BigDecimal calculateTotal(Collection<CartEntry> cartEntries) {
        BigDecimal total = BigDecimal.ZERO;

        for (CartEntry e : cartEntries) {
            BigDecimal price = BigDecimal.valueOf(e.getPrice());     // convert from double safely

            total = total.add(price);
        }
        return total;
    }



    @Override
   @Transactional(readOnly = true)
    @Cacheable(value = "cartSummary", key = "#userId")
    public CartSummary getCartSummaryByUserId(Long userId) {
        log.info("Cache miss - fetching cart from database for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CartServiceException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartServiceException("Cart not found"));

        Collection<CartEntry> cartEntrys = cart.getCartEntries();

        CartSummary cartSummary = new CartSummary();
        cartSummary.setCartId(cart.getId());
        Collection<CartEntryDto> cartEntryDtos= CartEntryDtoMapper.mapToCartEntryDto(cartEntrys);
        cartSummary.setEntrys(cartEntryDtos);
        cartSummary.setTotalQuantity(cartEntrys.size());
        cartSummary.setTotalPrice(cart.getTotal());
        cartSummary.setUser(UserDtoMapper.mapToUserDto(user));

        return cartSummary;
    }


}
