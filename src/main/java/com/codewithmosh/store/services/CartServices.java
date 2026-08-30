package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.exceptions.CartNotFoundExceptions;
import com.codewithmosh.store.exceptions.ProductNotFoundExceptions;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartServices {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;


    //Create Cart
    public CartDto createCart(){
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    //Add to cart item
    public CartItemDto addToCart(UUID cartId, Long productId){
        var cart =  cartRepository.getCartsWithItems(cartId).orElse(null);
        if (cart == null) {
           throw new CartNotFoundExceptions();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundExceptions();
        }

        var cartItem =  cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);

    }


    //Getting a cart

    public CartDto getCart(UUID cartId){
        var cart = cartRepository.getCartsWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundExceptions();
        }
        return cartMapper.toDto(cart);
    }

    //Update Cart Items in the cart
    public CartItemDto updateItem(UUID cartId, Long productId, Integer quantity){
        var cart = cartRepository.getCartsWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundExceptions();
        }

        var cartItem = cart.getItem(productId);

        if (cartItem == null) {
            throw new ProductNotFoundExceptions();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);

    }

    //Remove Item from Cart
    public void removeItem(UUID cartId, Long productId){
        var cart = cartRepository.getCartsWithItems(cartId).orElse(null);
        if(cart == null) {
            throw new CartNotFoundExceptions();
        }

        cart.removeItem(productId);;
        cartRepository.save(cart);
    }

    //clear the cart
    public void clearCart(UUID cartId){
        var cart = cartRepository.getCartsWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundExceptions();
        }
        cart.clearItem();
        cartRepository.save(cart);
    }

}
