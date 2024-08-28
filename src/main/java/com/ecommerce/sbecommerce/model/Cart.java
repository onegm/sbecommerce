package com.ecommerce.sbecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL) //, orphanRemoval = true
    private List<CartItem> cartItems = new ArrayList<>();

    private double totalPrice;

    public void updateTotalPrice() {
        double total = 0.0;
        for(CartItem item : cartItems){
            total += item.getDiscountedPrice() * item.getQuantity();
        }
        totalPrice = total;
    }

    public void addItem(CartItem cartItem) {
        int index = cartItems.indexOf(cartItem);
        if(index == -1)
            cartItems.add(cartItem);
        else
            cartItems.set(index, cartItem);
        updateTotalPrice();
    }

    public void updateCartItem(CartItem cartItem){
        int index = cartItems.indexOf(cartItem);
        if(index == -1)
            return;
        if(cartItem.getQuantity() <= 0)
            cartItems.remove(cartItem);
        else
            cartItems.set(index, cartItem);

        updateTotalPrice();
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        updateTotalPrice();
    }

    @PreRemove
    public void preRemove(){
        this.user = null;
    }
}
