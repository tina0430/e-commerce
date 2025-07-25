package kr.hhplus.be.ecommerce.common.domain.valueObject;

import io.micrometer.common.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserIdConverter implements Converter<String, UserId> {
    
    @Override
    public UserId convert(@NonNull String source) {
        return UserId.from(source);
    }
} 