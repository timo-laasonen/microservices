package com.microservice.productservice.service.impl;

import com.microservice.productservice.exception.ProductServiceCustomException;
import com.microservice.productservice.payload.request.ProductRequest;
import com.microservice.productservice.payload.response.ProductResponse;
import com.microservice.productservice.persistence.product.Product;
import com.microservice.productservice.persistence.product.ProductRepository;
import com.microservice.productservice.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("ProductServiceImpl | addProduct is called");

        final var product
            = Product.builder()
            .productName(productRequest.getName())
            .quantity(productRequest.getQuantity())
            .price(productRequest.getPrice())
            .build();

        final var savedProduct = productRepository.save(product);

        log.info("ProductServiceImpl | addProduct | Product Created");
        log.info("ProductServiceImpl | addProduct | Product Id : " + savedProduct.getId());
        return savedProduct.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long productId) {

        log.info("ProductServiceImpl | getProductById is called");
        log.info("ProductServiceImpl | getProductById | Get the product for productId: {}", productId);

        final var product
            = productRepository.findById(productId)
            .orElseThrow(
                () -> new ProductServiceCustomException("Product with given Id not found","PRODUCT_NOT_FOUND"));

        final var productResponse
            = new ProductResponse();

        copyProperties(product, productResponse);

        log.info("ProductServiceImpl | getProductById | productResponse :" + productResponse);

        return productResponse;
    }

    @Transactional
    @Override
    public void reduceQuantity(Long productId, Long quantity) {

        log.info("Reduce Quantity {} for Id: {}", quantity,productId);

        final var product
            = productRepository.findById(productId)
            .orElseThrow(() -> new ProductServiceCustomException(
                "Product with given Id not found",
                "PRODUCT_NOT_FOUND"
            ));

        if(product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                "Product does not have sufficient Quantity",
                "INSUFFICIENT_QUANTITY"
            );
        }

        product.setQuantity(product.getQuantity() - quantity);
        log.info("Product Quantity updated Successfully");
    }

    @Transactional
    @Override
    public void deleteProductById(Long productId) {
        log.info("Product id: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.info("Im in this loop {}", !productRepository.existsById(productId));
            throw new ProductServiceCustomException(
                "Product with given with Id: " + productId + " not found:",
                "PRODUCT_NOT_FOUND");
        }
        log.info("Deleting Product with id: {}", productId);
        productRepository.deleteById(productId);

    }
}
