package kr.hhplus.be.ecommerce.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payment 도메인 객체
 * 결제 정보를 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long paymentId;
    private Long orderId;
    private Integer totalAmount;
    private Integer discountAmount;
    private Integer finalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateStatus(PaymentStatus status) {
        this.paymentStatus = status;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return this.paymentStatus == PaymentStatus.SUCCESS;
    }

    public boolean isFailed() {
        return this.paymentStatus == PaymentStatus.FAILED;
    }

} 