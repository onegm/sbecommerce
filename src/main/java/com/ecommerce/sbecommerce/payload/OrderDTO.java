package com.ecommerce.sbecommerce.payload;

import com.ecommerce.sbecommerce.model.Address;
import com.ecommerce.sbecommerce.model.OrderItem;
import com.ecommerce.sbecommerce.model.Payment;
import com.ecommerce.sbecommerce.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String userEmail;
    private List<OrderItemDTO> items = new ArrayList<>();
    private PaymentDTO payment;
    private LocalDate orderDate;
    private Long addressId;
    private Double totalPrice;
    private String status;
}
