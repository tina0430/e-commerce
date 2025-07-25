package kr.hhplus.be.ecommerce.payment.domain.model;

public enum PaymentStatus {
    /**
     * 결제 대기
     */
    PENDING,
    /**
     * 결제 성공
     */
    SUCCESS,
    /**
     * 결제 실패
     */
    FAILED
}