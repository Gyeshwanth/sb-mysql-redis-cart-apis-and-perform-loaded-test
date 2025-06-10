package com.yeshwanth.sbr.repository;

import com.yeshwanth.sbr.model.Cart;
import com.yeshwanth.sbr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartEntries entries " +
            "WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithEntries(@Param("userId") Long userId);
}
