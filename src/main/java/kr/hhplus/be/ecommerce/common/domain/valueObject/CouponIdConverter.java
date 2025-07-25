package kr.hhplus.be.ecommerce.common.domain.valueObject;

import io.micrometer.common.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CouponIdConverter implements Converter<String, CouponId> {

    @Override
    public CouponId convert(@NonNull String source) {
        return CouponId.from(source);
    }
}