package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundExceptions;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutServices {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthServices authServices;
    private final CartServices cartServices;

    @Value("${webSiteUrl}")
    private String websiteurl;

    public CheckoutResponse checkOut(CheckoutRequest request) throws StripeException {
        var cart = cartRepository.getCartsWithItems(request.getCartId()).orElse(null);
        if(cart == null) {
           throw new CartNotFoundExceptions();
        }

        if(cart.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authServices.getCurrentUser());
        orderRepository.save(order);

        //create checkout session with stripe
        var builder =  SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(websiteurl+"/success.html")
                        .setCancelUrl(websiteurl+"/cancel.html");

        order.getItems().forEach(item -> {
           var lineItem =  SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmountDecimal(item.getUnitPrice())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProduct().getName())
                                                    .build()
                                    )
                                    .build()


                    ).build();
            builder.addLineItem(lineItem);
        });

        var session = Session.create(builder.build());

        cartServices.clearCart(cart.getId());


        return new CheckoutResponse(order.getId(), session.getUrl());
    }
}
