package kr.hhplus.be.ecommerce.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystemException(SystemException ex) {
        return ResponseEntity
                .status(ex.getError().getStatus())
                .body(new ErrorResponse(ex.getError().getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(SystemError.INVALID_PARAMETER.getStatus())
                .body(new ErrorResponse(SystemError.INVALID_PARAMETER.getCode(), SystemError.INVALID_PARAMETER.getMessage()));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity
                .status(SystemError.INVALID_NUMBER_FORMAT.getStatus())
                .body(new ErrorResponse(SystemError.INVALID_NUMBER_FORMAT.getCode(), SystemError.INVALID_NUMBER_FORMAT.getMessage()));
    }

    // 예상하지 못한 모든 예외에 대한 방어
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        return ResponseEntity
                .status(SystemError.UNKNOWN_ERROR.getStatus())
                .body(new ErrorResponse(SystemError.UNKNOWN_ERROR.getCode(), SystemError.UNKNOWN_ERROR.getMessage()));
    }
}
