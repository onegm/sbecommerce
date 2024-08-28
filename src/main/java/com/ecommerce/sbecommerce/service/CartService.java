package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.Cart;
import com.ecommerce.sbecommerce.model.CartItem;
import com.ecommerce.sbecommerce.model.Product;
import com.ecommerce.sbecommerce.payload.CartDTO;
import com.ecommerce.sbecommerce.payload.CartResponse;
import com.ecommerce.sbecommerce.repository.CartRepository;
import com.ecommerce.sbecommerce.security.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemService cartItemService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    AuthUtil authUtil;
    @Autowired
    ModelMapper modelMapper;

    public CartDTO addProduct(Long productId, int quantity) {
        Product product = productService.findById(productId);
        if(product.getQuantity() == 0)
            throw new APIException(product.getName() + " is out of stock.");

        Cart cart = findOrCreateCart();
        CartItem cartItem = cartItemService.findOrCreateCartItem(productId, cart.getId());

        if(product.getQuantity() < cartItem.getQuantity() + quantity)
            throw new APIException(product.getName() + " is not available at at the selected quantity: " + (cartItem.getQuantity() + quantity));

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setPrice(product.getPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItem = cartItemService.update(cartItem);

        cart.addItem(cartItem);

        return modelMapper.map(cartRepository.save(cart), CartDTO.class);
    }

    public Cart findOrCreateCart(){
        Cart cart = cartRepository.findCartByUserId(authUtil.loggedInUserId())
                .orElse(new Cart());
        if(cart.getUser() != null)
            return cart;
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

    public CartResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Cart> cartPage = cartRepository.findAll(pageDetails);
        List<Cart> carts = cartPage.getContent();
        if(carts.isEmpty())
            throw new APIException("No carts have been created.");

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> modelMapper.map(cart, CartDTO.class))
                .toList();

        CartResponse cartResponse = new CartResponse(cartDTOS);
        cartResponse.setPageNumber(cartPage.getNumber());
        cartResponse.setPageSize(cartPage.getSize());
        cartResponse.setTotalElements(cartPage.getTotalElements());
        cartResponse.setTotalPages(cartPage.getTotalPages());
        cartResponse.setLastPage(cartPage.isLast());
        return cartResponse;
    }

    public CartDTO getLoggedInUserCart() {
        Cart userCart = cartRepository.findCartByUserId(authUtil.loggedInUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "UserID", authUtil.loggedInUserId()));
        return modelMapper.map(userCart, CartDTO.class);
    }

    @Transactional
    public CartDTO incrementLoggedInCartItem(Long productId, int value) {
        Cart userCart = cartRepository.findCartByUserId(authUtil.loggedInUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "UserID", authUtil.loggedInUserId()));
        productService.findById(productId);
        CartItem cartItem = cartItemService.findByProductAndCart(productId, userCart.getId());


        cartItemService.increment(cartItem.getId(), value);
        userCart.updateTotalPrice();

        return modelMapper.map(userCart, CartDTO.class);
    }


    public CartDTO deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        productService.findById(productId);
        CartItem cartItem = cartItemService.findByProductAndCart(productId, cartId);
        cartItemService.delete(cartItem);
        return modelMapper.map(cartRepository.save(cart), CartDTO.class);
    }

    public void emptyCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).
                orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        cartItemService.deleteAll(cart.getCartItems());
    }
}
