package com.microservice.orderservice.external.client;

import com.microservice.commonmodels.payload.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8083/payments")
public interface PaymentService {

    @PostMapping
    ResponseEntity<Long> doPayment(
        @RequestBody PaymentRequest paymentRequest
    );
}
