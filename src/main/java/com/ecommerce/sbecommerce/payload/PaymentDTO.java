package com.ecommerce.sbecommerce.payload;

import com.ecommerce.sbecommerce.model.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String method;
    private String paymentGatewayId;
    private String paymentGatewayName;
    private String paymentGatewayStatus;
    private String paymentGatewayResponseMessage;
}
