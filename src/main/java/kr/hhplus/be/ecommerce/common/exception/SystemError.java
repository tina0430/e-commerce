package kr.hhplus.be.ecommerce.common.exception;

import kr.hhplus.be.ecommerce.common.exception.base.ErrorBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SystemError implements ErrorBase {

    /**
     * 공통
     */
    UNKNOWN_ERROR("SY000", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
