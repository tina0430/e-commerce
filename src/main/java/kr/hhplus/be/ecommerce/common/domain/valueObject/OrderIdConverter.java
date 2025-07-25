package kr.hhplus.be.ecommerce.common.domain.valueObject;

import io.micrometer.common.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderIdConverter implements Converter<String, OrderId> {

    @Override
    public OrderId convert(@NonNull String source) {
        return OrderId.from(source);
    }
}