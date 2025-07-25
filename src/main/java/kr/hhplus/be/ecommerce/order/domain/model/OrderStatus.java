package kr.hhplus.be.ecommerce.order.domain.model;

public enum OrderStatus {
    /**
     * 대기중
     */
    PENDING,
    /**
     * 확정
     */
    CONFIRMED,
    /**
     * 배송중 fixme 이거 필요 없을듯?
     */
    SHIPPING,
    /**
     * 배송완료
     */
    DELIVERED,
    /**
     * 취소됨
     */
    CANCELLED,
    /**
     * 환불됨
     */
    REFUNDED
} 