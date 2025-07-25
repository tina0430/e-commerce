package kr.hhplus.be.ecommerce.coupon.domain.model;

public enum CouponPolicyStatus {
    /**
     * 발행 대기
     */
    PENDING,
    /**
     * 발행 중
     */
    ACTIVE,
    /**
     * 발행 종료
     */
    ENDED,
    /**
     * 발급 완료
     */
    EXHAUSTED
}
