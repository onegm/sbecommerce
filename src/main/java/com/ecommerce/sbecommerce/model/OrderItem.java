package com.ecommerce.sbecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private Integer quantity;
    private Double salePrice;

    public OrderItem(CartItem cartItem) {
        this.product = cartItem.getProduct();
        this.quantity = cartItem.getQuantity();
        this.salePrice = cartItem.getDiscountedPrice();
    }
}
