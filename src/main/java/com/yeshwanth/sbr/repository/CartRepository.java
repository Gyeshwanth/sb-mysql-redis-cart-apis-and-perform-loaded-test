package com.yeshwanth.sbr.repository;

import com.yeshwanth.sbr.model.Cart;
import com.yeshwanth.sbr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);


}
