package kr.hhplus.be.ecommerce.common.domain.valueObject;

import jakarta.validation.constraints.Min;

public record CouponId(@Min(1) Long value) {

    /**
     * String에서 OrderId로 변환하는 팩토리 메서드
     */
    public static CouponId from(String value) {
        return new CouponId(Long.valueOf(value));
    }

    /**
     * Long에서 OrderId로 변환하는 팩토리 메서드
     */
    public static CouponId from(Long value) {
        return new CouponId(value);
    }
}