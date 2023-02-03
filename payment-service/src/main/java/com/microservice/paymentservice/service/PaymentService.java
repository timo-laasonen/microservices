package com.microservice.paymentservice.service;

import com.microservice.paymentservice.payload.request.PaymentRequest;
import com.microservice.paymentservice.payload.response.PaymentResponse;

public interface PaymentService {

    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(Long orderId);
}
