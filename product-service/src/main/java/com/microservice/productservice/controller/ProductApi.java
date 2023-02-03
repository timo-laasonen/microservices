package com.microservice.productservice.controller;

import com.microservice.productservice.payload.request.ProductRequest;
import com.microservice.productservice.payload.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/products")
public interface ProductApi {
    @PostMapping
    ResponseEntity<Long> addProduct(
        @RequestBody ProductRequest productRequest
    );

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(
        @PathVariable("id") Long productId
    );

    @PutMapping("/reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(
        @PathVariable("id") Long productId,
        @RequestParam Long quantity
    );

    @DeleteMapping("/{id}")
    void deleteProductById(@PathVariable("id") Long productId);
}
