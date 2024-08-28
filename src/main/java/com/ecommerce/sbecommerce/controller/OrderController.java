package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.model.User;
import com.ecommerce.sbecommerce.payload.OrderDTO;
import com.ecommerce.sbecommerce.payload.OrderRequestDTO;
import com.ecommerce.sbecommerce.security.AuthUtil;
import com.ecommerce.sbecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/order")
    public ResponseEntity<OrderDTO> orderProducts(@RequestBody OrderRequestDTO orderRequestDTO){
        User user = authUtil.loggedInUser();
        return new ResponseEntity<>(orderService.placeOrder(user, orderRequestDTO), HttpStatus.CREATED);
    }
}
