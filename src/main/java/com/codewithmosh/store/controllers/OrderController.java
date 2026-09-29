package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.dtos.OrderDto;

import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.services.OrderServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderServices orderServices;


    @GetMapping
    public List<OrderDto> getAllOrders() {

        // Implementation to retrieve all orders
        return orderServices.getAllOrders();
    }

    @GetMapping({"/{orderId}"})
    public OrderDto getOrder(@PathVariable("orderId") Long orderId) {
        return orderServices.getOrder(orderId);
    }

    @ExceptionHandler({OrderNotFoundException.class})
    ResponseEntity<Void> handleOrderNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({AccessDeniedException.class})
    ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto(ex.getMessage()));
    }

}
