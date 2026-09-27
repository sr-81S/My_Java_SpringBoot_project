package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundExceptions;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckoutServices {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthServices authServices;
    private final CartServices cartServices;

    public CheckoutResponse checkOut(CheckoutRequest request) {
        var cart = cartRepository.getCartsWithItems(request.getCartId()).orElse(null);
        if(cart == null) {
           throw new CartNotFoundExceptions();
        }

        if(cart.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authServices.getCurrentUser());
        orderRepository.save(order);
        cartServices.clearCart(cart.getId());


        return new CheckoutResponse(order.getId());
    }
}
