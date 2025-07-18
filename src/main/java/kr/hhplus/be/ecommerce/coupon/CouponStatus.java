package kr.hhplus.be.ecommerce.coupon;

public enum CouponStatus {
    /**
     * 발급 가능
     */
    AVAILABLE,
    /**
     * 발급 완료
     */
    ISSUED,
    /**
     * 사용 완료
     */
    USED,
    /**
     * 기간 만료
     */
    EXPIRED
}