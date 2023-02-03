package com.microservice.paymentservice.controller;

import com.microservice.paymentservice.payload.request.PaymentRequest;
import com.microservice.paymentservice.payload.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/payments")
public interface PaymentApi {
    @PostMapping
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    @GetMapping("/order/{orderId}")
    ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable Long orderId);
}
