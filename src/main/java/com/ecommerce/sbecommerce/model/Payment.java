package com.ecommerce.sbecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonIgnore
    private Order order;

    private String method;

    private String paymentGatewayId;
    private String paymentGatewayName;
    private String paymentGatewayStatus;
    private String paymentGatewayResponseMessage;

    public Payment(Order order, String method, String paymentGatewayId, String paymentGatewayName, String paymentGatewayStatus, String paymentGatewayResponseMessage) {
        this.order = order;
        this.method = method;
        this.paymentGatewayId = paymentGatewayId;
        this.paymentGatewayName = paymentGatewayName;
        this.paymentGatewayStatus = paymentGatewayStatus;
        this.paymentGatewayResponseMessage = paymentGatewayResponseMessage;
    }
}
