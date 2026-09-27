package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundExceptions;
import com.codewithmosh.store.services.CheckoutServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {

    private final CheckoutServices checkoutServices;


    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutServices.checkOut(request);
    }

    @ExceptionHandler({CartNotFoundExceptions.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handelExceptions(Exception ex) {
        var errorDto = new ErrorDto(ex.getMessage());
        return ResponseEntity.badRequest().body(errorDto);
    }

}
