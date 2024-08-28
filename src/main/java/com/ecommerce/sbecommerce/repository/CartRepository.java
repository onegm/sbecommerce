package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Optional<Cart> findByEmail(String email);
}
