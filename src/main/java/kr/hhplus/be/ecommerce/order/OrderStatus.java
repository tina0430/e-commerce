package kr.hhplus.be.ecommerce.order;

public enum OrderStatus {
    /**
     * 결제 대기
     */
    PENDING,
    /**
     * 결제 완료
     */
    COMPLETED,
    /**
     * 주문 취소
     */
    CANCELED
}