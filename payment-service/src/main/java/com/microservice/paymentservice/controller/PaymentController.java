package com.microservice.paymentservice.controller;

import com.microservice.paymentservice.payload.request.PaymentRequest;
import com.microservice.paymentservice.payload.response.PaymentResponse;
import com.microservice.paymentservice.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public ResponseEntity<Long> doPayment(final PaymentRequest paymentRequest) {

        log.info("PaymentController | doPayment is called");

        log.info("PaymentController | doPayment | paymentRequest : " + paymentRequest.toString());

        return new ResponseEntity<>(
            paymentService.doPayment(paymentRequest),
            HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(Long orderId) {

        log.info("PaymentController | doPayment is called");

        log.info("PaymentController | doPayment | orderId : " + orderId);

        return new ResponseEntity<>(
            paymentService.getPaymentDetailsByOrderId(orderId),
            HttpStatus.OK
        );
    }
}
