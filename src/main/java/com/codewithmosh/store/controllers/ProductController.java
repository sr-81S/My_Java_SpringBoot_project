package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(name="categoryId", required = false) Byte categoryId
    ) {

        List<Product> products;
        if(categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findProductsWithCategory();
        }
        return products.stream().map(productMapper::toDto).toList();
    }

    //get the product by id
     @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
     }


     //Create a product
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriComponentsBuilder) {
             var category =  categoryRepository.findById(productDto.getCategoryId()).orElse(null);

            if(category == null) {
                return ResponseEntity.badRequest().build();
            }

            var product = productMapper.toEntity(productDto);
            product.setCategory(category);
            var savedProduct = productRepository.save(product);
            var uri = uriComponentsBuilder.path("/{id}").buildAndExpand(savedProduct.getId()).toUri();
            return ResponseEntity.created(uri).body(productMapper.toDto(savedProduct));
     }

     //Update the product
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable(name ="id") Long id, @RequestBody ProductDto productDto) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if(category == null) {
            return ResponseEntity.notFound().build();
        }

        var product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productMapper.update(productDto, product);
        product.setCategory(category);
        var updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(productMapper.toDto(updatedProduct));
    }

     //Delete the product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name="id") Long id) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
