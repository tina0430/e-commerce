package kr.hhplus.be.ecommerce.common.domain.valueObject;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;

public record UserId(@Min(1) Long value) {
    
    /**
     * String에서 UserId로 변환하는 팩토리 메서드
     */
    public static UserId from(String value) {
        return new UserId(Long.valueOf(value));
    }
    
    /**
     * Long에서 UserId로 변환하는 팩토리 메서드
     */
    public static UserId from(Long value) {
        return new UserId(value);
    }
}
