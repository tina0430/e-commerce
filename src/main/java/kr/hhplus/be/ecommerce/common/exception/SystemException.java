package kr.hhplus.be.ecommerce.common.exception;

import kr.hhplus.be.ecommerce.common.exception.base.ExceptionBase;

public class SystemException extends ExceptionBase {

    private final SystemError error;

    public SystemException(SystemError error) {
        this(error, error.getMessage());
    }

    public SystemException(SystemError error, String message) {
        super(message);
        this.error = error;
    }

    @Override
    public SystemError getError() {
        return error;
    }

}
