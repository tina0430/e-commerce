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
    private Long originalPrice;
    private Long discountAmount;
    private Long finalPrice;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.SUCCESS;
    }

    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }

} 