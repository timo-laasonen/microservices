package com.microservice.orderservice.service.impl;

import com.microservice.commonmodels.payload.request.PaymentRequest;
import com.microservice.orderservice.exception.OrderServiceCustomException;
import com.microservice.orderservice.external.client.PaymentService;
import com.microservice.orderservice.external.client.ProductService;
import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.payload.response.PaymentResponse;
import com.microservice.orderservice.payload.response.ProductResponse;
import com.microservice.orderservice.persistence.Order;
import com.microservice.orderservice.persistence.OrderRepository;
import com.microservice.orderservice.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate;

    private final ProductService productService;

    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(
        final OrderRepository orderRepository,
        final RestTemplateBuilder restTemplateBuilder,
        final ProductService productService,
        final PaymentService paymentService
    ) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Transactional
    @Override
    public Long placeOrder(OrderRequest orderRequest) {

        log.info("OrderServiceImpl | placeOrder is called");

        //Order Entity -> Save the data with Status Order Created
        //Product Service - Block Products (Reduce the Quantity)
        //Payment Service -> Payments -> Success-> COMPLETE, Else
        //CANCELLED

        log.info("OrderServiceImpl | placeOrder | Placing Order Request orderRequest : " + orderRequest.toString());

        log.info("OrderServiceImpl | placeOrder | Calling productService through FeignClient");
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("OrderServiceImpl | placeOrder | Creating Order with Status CREATED");
        final var order = Order.builder()
            .amount(orderRequest.getTotalAmount())
            .orderStatus("CREATED")
            .productId(orderRequest.getProductId())
            .orderDate(Instant.now())
            .quantity(orderRequest.getQuantity())
            .build();

        final var savedOrder = orderRepository.save(order);

        log.info("OrderServiceImpl | placeOrder | Calling Payment Service to complete the payment");

        final var paymentRequest
            = PaymentRequest.builder()
            .orderId(order.getId())
            .paymentMode(orderRequest.getPaymentMode())
            .amount(orderRequest.getTotalAmount())
            .build();

        try {
            paymentService.doPayment(paymentRequest);
            log.info("OrderServiceImpl | placeOrder | Payment done Successfully. Changing the Oder status to PLACED");
            savedOrder.setOrderStatus("PLACED");
        } catch (Exception e) {
            log.error("OrderServiceImpl | placeOrder | Error occurred in payment. Changing order status to PAYMENT_FAILED");
            savedOrder.setOrderStatus("PAYMENT_FAILED");
        }

        log.info("OrderServiceImpl | placeOrder | Order Places successfully with Order Id: {}", savedOrder.getId());

        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderDetails(Long orderId) {

        log.info("OrderServiceImpl | getOrderDetails | Get order details for Order Id : {}", orderId);

        final var order
            = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderServiceCustomException("Order not found for the order Id:" + orderId,
                "NOT_FOUND",
                404));

        log.info("OrderServiceImpl | getOrderDetails | Invoking Product service to fetch the product for id: {}", order.getProductId());
        final var productResponse
            = restTemplate.getForObject(
            "http://product-service/products/" + order.getProductId(),
            ProductResponse.class
        );

        log.info("OrderServiceImpl | getOrderDetails | Getting payment information form the payment Service");
        final var paymentResponse
            = restTemplate.getForObject(
            "http://payment-service/payments/order/" + order.getId(),
            PaymentResponse.class
        );

        final var productDetails
            = OrderResponse.ProductDetails
            .builder()
            .productName(Optional.ofNullable(productResponse).map(ProductResponse::getProductName).orElse(null))
            .productId(Optional.ofNullable(productResponse).map(ProductResponse::getId).orElse(null))
            .build();

        final var paymentDetails
            = OrderResponse.PaymentDetails
            .builder()
            .paymentId(Optional.ofNullable(paymentResponse).map(PaymentResponse::getPaymentId).orElse(null))
            .paymentStatus(Optional.ofNullable(paymentResponse).map(PaymentResponse::getStatus).orElse(null))
            .paymentDate(Optional.ofNullable(paymentResponse).map(PaymentResponse::getPaymentDate).orElse(null))
            .paymentMode(Optional.ofNullable(paymentResponse).map(PaymentResponse::getPaymentMode).orElse(null))
            .build();

        final var orderResponse
            = OrderResponse.builder()
            .orderId(order.getId())
            .orderStatus(order.getOrderStatus())
            .amount(order.getAmount())
            .orderDate(order.getOrderDate())
            .productDetails(productDetails)
            .paymentDetails(paymentDetails)
            .build();

        log.info("OrderServiceImpl | getOrderDetails | orderResponse : " + orderResponse.toString());

        return orderResponse;
    }
}
