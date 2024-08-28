package com.ecommerce.sbecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    public Long addressId;
    private String method;
    private String paymentGatewayId;
    private String paymentGatewayName;
    private String paymentGatewayStatus;
    private String paymentGatewayResponseMessage;
}
