package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.exceptions.CartNotFoundExceptions;
import com.codewithmosh.store.exceptions.ProductNotFoundExceptions;
import com.codewithmosh.store.services.CartServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
@Tag(name = "Cart Controller", description = "APIs for managing shopping carts")
public class CartController {

    private final CartServices cartServices;

    @PostMapping
    @Operation(summary = "Create a new cart", description = "Creates a new shopping cart and returns its details.")
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriComponentsBuilder
    ) {
       var cartdto = cartServices.createCart();
        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartdto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartdto);
    }

    //Add to cart API controller
    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add item to cart", description = "Adds an item to the specified shopping cart.")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
           @RequestBody AddItemToCartRequest request
            ){

        var cartItemDto = cartServices.addToCart(cartId, request.getProductId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    //endpoint for getting cart
    @GetMapping("/{cartId}")
    @Operation(summary = "Get Cart Details", description = "Retrieves the details of the specified shopping cart.")
    public ResponseEntity<CartDto> getCart(
            @PathVariable UUID cartId
    ){
        var cartDto = cartServices.getCart(cartId);
        return ResponseEntity.ok(cartDto);
    }

    //Update cart Item
    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Update Cart Item", description = "Updates the quantity of an item in the specified shopping cart.")
    public ResponseEntity<?> updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ){
        var cartItemDto = cartServices.updateItem(cartId, productId, request.getQuantity());
        return ResponseEntity.ok(cartItemDto);
    }

    //Delete cart item

    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Remove Item from Cart", description = "Removes an item from the specified shopping cart.")
    public ResponseEntity<?> removeItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId
    ){
        cartServices.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }


    //clear the cart

    @DeleteMapping("/{cartId}")
    @Operation(summary = "Clear Cart", description = "Clears all items from the specified shopping cart.")
    public ResponseEntity<?> clearCart(@PathVariable UUID cartId){
        cartServices.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    //controller exception handeler for all controller
    @ExceptionHandler({CartNotFoundExceptions.class})
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart not found"));
    }

    @ExceptionHandler({ProductNotFoundExceptions.class})
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product not found in the cart"));
    }

}
