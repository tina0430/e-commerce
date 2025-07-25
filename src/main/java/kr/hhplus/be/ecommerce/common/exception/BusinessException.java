package kr.hhplus.be.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final BusinessError businessError;

    public BusinessException(BusinessError businessError) {
        this(businessError, businessError.getMessage());
    }

    public BusinessException(BusinessError businessError, String message) {
        super(message);
        this.businessError = businessError;
    }

    public String getCode() {
        return businessError.getCode();
    }

    public HttpStatus getStatus() {
        return businessError.getStatus();
    }
}
