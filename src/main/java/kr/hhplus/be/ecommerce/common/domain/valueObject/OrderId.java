package kr.hhplus.be.ecommerce.common.domain.valueObject;

import jakarta.validation.constraints.Min;

public record OrderId(@Min(1) Long value) {

    /**
     * String에서 OrderId로 변환하는 팩토리 메서드
     */
    public static OrderId from(String value) {
        return new OrderId(Long.valueOf(value));
    }

    /**
     * Long에서 OrderId로 변환하는 팩토리 메서드
     */
    public static OrderId from(Long value) {
        return new OrderId(value);
    }
}