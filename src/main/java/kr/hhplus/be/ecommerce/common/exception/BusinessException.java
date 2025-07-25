package kr.hhplus.be.ecommerce.common.exception;

import kr.hhplus.be.ecommerce.common.exception.base.ExceptionBase;

public class BusinessException extends ExceptionBase {

    private final BusinessError error;

    public BusinessException(BusinessError error) {
        this(error, error.getMessage());
    }

    public BusinessException(BusinessError error, String message) {
        super(message);
        this.error = error;
    }

    @Override
    public BusinessError getError() {
        return error;
    }

}
