package com.microservice.paymentservice.service.impl;

import com.microservice.paymentservice.exception.PaymentServiceCustomException;
import com.microservice.paymentservice.payload.request.PaymentRequest;
import com.microservice.paymentservice.payload.response.PaymentResponse;
import com.microservice.paymentservice.persistence.transaction.TransactionDetails;
import com.microservice.paymentservice.persistence.transaction.TransactionDetailsRepository;
import com.microservice.paymentservice.service.PaymentService;
import com.microservice.paymentservice.utils.PaymentMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final TransactionDetailsRepository transactionDetailsRepository;

    public PaymentServiceImpl(TransactionDetailsRepository transactionDetailsRepository) {
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Transactional
    @Override
    public Long doPayment(PaymentRequest paymentRequest) {

        log.info("PaymentServiceImpl | doPayment is called");

        log.info("PaymentServiceImpl | doPayment | Recording Payment Details: {}", paymentRequest);

        final var transactionDetails
            = TransactionDetails.builder()
            .paymentDate(Instant.now())
            .paymentMode(paymentRequest.getPaymentMode().name())
            .paymentStatus("SUCCESS")
            .orderId(paymentRequest.getOrderId())
            .referenceNumber(paymentRequest.getReferenceNumber())
            .amount(paymentRequest.getAmount())
            .build();

        final var savedTransactionDetails = transactionDetailsRepository.save(transactionDetails);

        log.info("Transaction Completed with Id: {}", savedTransactionDetails.getId());

        return savedTransactionDetails.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentDetailsByOrderId(Long orderId) {

        log.info("PaymentServiceImpl | getPaymentDetailsByOrderId is called");

        log.info("PaymentServiceImpl | getPaymentDetailsByOrderId | Getting payment details for the Order Id: {}", orderId);

        TransactionDetails transactionDetails
            = transactionDetailsRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentServiceCustomException(
                "TransactionDetails with given id not found",
                "TRANSACTION_NOT_FOUND"));

        PaymentResponse paymentResponse
            = PaymentResponse.builder()
            .paymentId(transactionDetails.getId())
            .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
            .paymentDate(transactionDetails.getPaymentDate())
            .orderId(transactionDetails.getOrderId())
            .status(transactionDetails.getPaymentStatus())
            .amount(transactionDetails.getAmount())
            .build();

        log.info("PaymentServiceImpl | getPaymentDetailsByOrderId | paymentResponse: {}", paymentResponse.toString());

        return paymentResponse;
    }
}
