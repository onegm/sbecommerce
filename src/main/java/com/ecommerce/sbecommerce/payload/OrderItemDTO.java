package com.ecommerce.sbecommerce.payload;

import com.ecommerce.sbecommerce.model.Order;
import com.ecommerce.sbecommerce.model.Product;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private ProductDTO product;
    private Integer quantity;
    private Double salePrice;
}
