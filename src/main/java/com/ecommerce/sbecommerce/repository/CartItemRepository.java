package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.CartItem;
import com.ecommerce.sbecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductIdAndCartId(Long productId, Long cartId);

    List<CartItem> findAllByProductId(Long productId);

    List<CartItem> findByCartId(Long cartId);
}
