package com.microservice.productservice.controller;

import com.microservice.productservice.payload.request.ProductRequest;
import com.microservice.productservice.payload.response.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.productservice.service.ProductService;

@RestController
@Log4j2
public class ProductController implements ProductApi {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<Long> addProduct(final ProductRequest productRequest) {

        log.info("ProductController | addProduct is called");

        log.info("ProductController | addProduct | productRequest : " + productRequest.toString());

        return new ResponseEntity<>(
            productService.addProduct(productRequest),
            HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(
        final Long productId
    ) {

        log.info("ProductController | getProductById is called");

        log.info("ProductController | getProductById | productId : " + productId);

        return new ResponseEntity<>(
            productService.getProductById(productId),
            HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Void> reduceQuantity(
        final Long productId,
        final Long quantity
    ) {

        log.info("ProductController | reduceQuantity is called");

        log.info("ProductController | reduceQuantity | productId : " + productId);
        log.info("ProductController | reduceQuantity | quantity : " + quantity);

        productService.reduceQuantity(productId,quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public void deleteProductById(final Long productId) {
        productService.deleteProductById(productId);
    }
}
