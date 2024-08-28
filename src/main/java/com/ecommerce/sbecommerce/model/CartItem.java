package com.ecommerce.sbecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Cart cart;

    private Double price;
    private Double discount;

    private Integer quantity = 0;

    public void setProduct(Product product){
        this.product = product;
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        if(quantity > product.getQuantity())
            this.quantity = product.getQuantity();
    }

    public Double getDiscountedPrice(){
        if (price == null || discount == null)
            return price;
        return price*(1-discount/100.0);
    }

    @Override
    public boolean equals(Object other){
        if(other == null || getClass()!= other.getClass())
            return false;
        CartItem otherItem = (CartItem) other;
        return id.equals(otherItem.getId());
    }

    @PreRemove
    public void preRemove(){
        cart.removeCartItem(this);
        setCart(null);
    }

}
