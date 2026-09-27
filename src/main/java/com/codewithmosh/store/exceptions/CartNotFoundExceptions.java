package com.codewithmosh.store.exceptions;

public class CartNotFoundExceptions extends RuntimeException {
    public CartNotFoundExceptions() {
        super("Cart is not found");
    }
   
}
