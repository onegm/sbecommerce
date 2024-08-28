package com.ecommerce.sbecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 50, message = "Name must contain between 3-50 characters.")
    private String name;
    private String image;
    @Size(min = 10, max = 120, message = "Description must contain between 10-120 characters.")
    private String description;
    private Integer quantity;
    @Min(value = 0, message = "Price must be at least 0.")
    private Double price;
    private Double discount;
    private Double specialPrice;

    @ManyToOne
    private Category category;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItem;

    public Double getDiscountedPrice(){
        if (price == null || discount == null)
            return price;
        return price*(1-discount/100.0);
    }

    public void setPrice(Double price){
        this.price = price;
        this.specialPrice = getDiscountedPrice();
    }
    public void setDiscount(Double discount){
        this.discount = discount;
        this.specialPrice = getDiscountedPrice();
    }
}
