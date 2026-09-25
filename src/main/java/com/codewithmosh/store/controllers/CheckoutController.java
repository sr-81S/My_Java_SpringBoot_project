package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.AuthServices;
import com.codewithmosh.store.services.CartServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {

    private final CartRepository cartRepository;
    private final AuthServices authServices;
    private final OrderRepository orderRepository;
    private final CartServices cartServices;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
        var cart = cartRepository.getCartsWithItems(request.getCartId()).orElse(null);
        if(cart == null) {
            return ResponseEntity.badRequest().body(
                new ErrorDto("cart not found")
            );
        }

        if(cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new ErrorDto("cart is empty")
            );
        }

        var order = Order.fromCart(cart, authServices.getCurrentUser());
        orderRepository.save(order);
        cartServices.clearCart(cart.getId());


        return ResponseEntity.ok().body(new CheckoutResponse(order.getId()));
    }

}
