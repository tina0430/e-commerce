package kr.hhplus.be.ecommerce.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class ExceptionBase extends RuntimeException {

    public ExceptionBase(String message) {
        super(message);
    }

    public abstract ErrorBase getError();

    public String getCode() {
        return getError().getCode();
    }

    public HttpStatus getStatus() {
        return getError().getStatus();
    }
}
