package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.config.AppConstants;
import com.ecommerce.sbecommerce.payload.CartDTO;
import com.ecommerce.sbecommerce.payload.CartResponse;
import com.ecommerce.sbecommerce.security.AuthUtil;
import com.ecommerce.sbecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    AuthUtil authUtil;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable int quantity){
        CartDTO cartDTO = cartService.addProduct(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<CartResponse> get(
                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
                @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending) {
        CartResponse cartResponse = cartService.get(pageNumber, pageSize, sortBy, sortAscending);
        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("/carts/user")
    public ResponseEntity<CartDTO> getCartByLoggedInUser(){
        return ResponseEntity.ok(cartService.getLoggedInUserCart());
    }

    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(@PathVariable Long productId,
                                                         @PathVariable boolean operation){
        return ResponseEntity.ok(cartService.incrementLoggedInCartItem(productId, (operation ? 1 : -1)));
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<CartDTO> deleteProductFromCart(@PathVariable Long cartId,
                                                         @PathVariable Long productId){
        return ResponseEntity.ok(cartService.deleteProductFromCart(cartId, productId));
    }

}
