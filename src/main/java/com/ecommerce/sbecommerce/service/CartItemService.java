package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.CartItem;
import com.ecommerce.sbecommerce.model.Product;
import com.ecommerce.sbecommerce.payload.ProductDTO;
import com.ecommerce.sbecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {
    @Autowired
    CartItemRepository cartItemRepository;

    public CartItem findOrCreateCartItem(Long productId, Long cart_id) {
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(productId, cart_id).orElse(null);
        if (cartItem != null)
            return cartItem;
        cartItem = new CartItem();
        return cartItemRepository.save(cartItem);
    }

    public CartItem findByProductAndCart(Long productId, Long cart_id) {
        return cartItemRepository.findByProductIdAndCartId(productId, cart_id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "cartId/productId", cart_id + "/" + productId));
    }

    public CartItem update(CartItem cartItem) {
        cartItemRepository.findById(cartItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItem.getId()));
        return cartItemRepository.save(cartItem);
    }

    public void increment(Long cartItemId, int value){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItemId));

        Product product = cartItem.getProduct();
        if(product.getQuantity() == 0)
            throw new APIException(product.getName() + " is out of stock.");
        if(product.getQuantity() < cartItem.getQuantity() + value)
            throw new APIException(product.getName() + " is not available at at the selected quantity: " + (cartItem.getQuantity() + value));

        if(cartItem.getQuantity() + value <= 0) {
            cartItemRepository.delete(cartItem);
            return;
        }
        cartItem.setQuantity(cartItem.getQuantity() + value);
    }

    public void delete(CartItem cartItem) {
        CartItem toDelete = cartItemRepository.findById(cartItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItem.getId()));
        cartItemRepository.delete(toDelete);
    }

    public void handleProductUpdate(Product updatedProduct) {
        List<CartItem> cartItems = cartItemRepository.findAllByProductId(updatedProduct.getId());
        cartItems.forEach(item -> {
            item.setProduct(updatedProduct);
            cartItemRepository.save(item);
            item.getCart().updateTotalPrice();
        });
    }

    public void handleProductDelete(Product updatedProduct) {
        List<CartItem> cartItems = cartItemRepository.findAllByProductId(updatedProduct.getId());
        cartItems.forEach(item -> cartItemRepository.delete(item));
    }

    public void deleteAll(List<CartItem> cartItems) {
        List<Long> ids = cartItems.stream().map(CartItem::getId).toList();
        cartItemRepository.deleteAllById(ids);
    }
}
