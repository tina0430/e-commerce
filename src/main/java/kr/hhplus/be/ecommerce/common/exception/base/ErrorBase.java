package kr.hhplus.be.ecommerce.common.exception.base;

import org.springframework.http.HttpStatus;

public interface ErrorBase {

    String getCode();
    HttpStatus getStatus();
    String getMessage();

}
