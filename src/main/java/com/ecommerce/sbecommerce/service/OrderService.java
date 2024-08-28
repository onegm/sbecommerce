package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.*;
import com.ecommerce.sbecommerce.payload.OrderDTO;
import com.ecommerce.sbecommerce.payload.OrderItemDTO;
import com.ecommerce.sbecommerce.payload.OrderRequestDTO;
import com.ecommerce.sbecommerce.payload.PaymentDTO;
import com.ecommerce.sbecommerce.repository.OrderItemRepository;
import com.ecommerce.sbecommerce.repository.OrderRepository;
import com.ecommerce.sbecommerce.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartService cartService;
    @Autowired
    AddressService addressService;
    @Autowired
    ProductService productService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public OrderDTO placeOrder(User user, OrderRequestDTO orderRequestDTO) {
        Cart cart = user.getCart();
        if(cart == null)
            throw new ResourceNotFoundException("Cart", "userId", user.getId());

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty())
            throw new APIException("Cart is empty.");

        Address address = addressService.getEntityById(orderRequestDTO.getAddressId());
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus("ACCEPTED");

        Payment payment = new Payment(
                order,
                orderRequestDTO.getMethod(),
                orderRequestDTO.getPaymentGatewayId(),
                orderRequestDTO.getPaymentGatewayName(),
                orderRequestDTO.getPaymentGatewayStatus(),
                orderRequestDTO.getPaymentGatewayResponseMessage()
                );
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem(cartItem);
            orderItem.setOrder(order);
            orderItem = orderItemRepository.save(orderItem);
            orderItems.add(orderItem);

            productService.reduceStock(cartItem.getProduct(), cartItem.getQuantity());
        }
        cartService.emptyCart(cart.getId());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(savedOrder.getId());
        orderDTO.setUserEmail(savedOrder.getUser().getEmail());
        orderDTO.setItems(savedOrder.getItems().stream().map((item) -> modelMapper.map(item, OrderItemDTO.class)).toList());
        orderDTO.setPayment(modelMapper.map(savedOrder.getPayment(), PaymentDTO.class));
        orderDTO.setOrderDate(savedOrder.getOrderDate());
        orderDTO.setAddressId(savedOrder.getAddress().getId());
        orderDTO.setTotalPrice(savedOrder.getTotalPrice());
        orderDTO.setStatus(savedOrder.getStatus());
        return orderDTO;
    }
}
