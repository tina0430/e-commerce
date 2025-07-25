package kr.hhplus.be.ecommerce.coupon.domain.model;

public enum UserCouponStatus {
    /**
     * 사용 가능
     */
    AVAILABLE,
    /**
     * 사용 완료
     */
    USED,
    /**
     * 기간 만료
     */
    EXPIRED
}