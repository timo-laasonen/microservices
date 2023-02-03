package com.microservice.orderservice.controller;

import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/orders")
public interface OrderApi {

    @PostMapping("/placeorder")
    ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest);

    @GetMapping("/{orderId}")
    ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId);
}
