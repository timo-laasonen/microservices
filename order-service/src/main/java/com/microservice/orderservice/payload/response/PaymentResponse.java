package com.microservice.orderservice.payload.response;

import com.microservice.commonmodels.payload.utils.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    private String status;
    private PaymentMode paymentMode;
    private Long amount;
    private Instant paymentDate;
    private Long orderId;
}
