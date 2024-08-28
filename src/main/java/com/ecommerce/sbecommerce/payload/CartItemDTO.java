package com.ecommerce.sbecommerce.payload;

import com.ecommerce.sbecommerce.model.Cart;
import com.ecommerce.sbecommerce.model.Product;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private ProductDTO product;
    private Double price;
    private Double discount;
    private Integer quantity;

}
