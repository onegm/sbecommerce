package com.ecommerce.sbecommerce.payload;

import com.ecommerce.sbecommerce.model.CartItem;
import com.ecommerce.sbecommerce.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private UserDTO user;
    private List<CartItemDTO> cartItems;
    private double totalPrice;
}
