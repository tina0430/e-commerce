package kr.hhplus.be.ecommerce.payment.presentation;

import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentDto {

    // O-3 상품 주문 결제
    public record PaymentResponse(Long paymentId,
                                  Integer totalAmount,
                                  Integer discountAmount,
                                  Integer finalAmount,
                                  PaymentStatus paymentStatus,
                                  LocalDateTime createdAt) {

    }

}