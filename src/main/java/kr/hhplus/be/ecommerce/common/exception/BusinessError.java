package kr.hhplus.be.ecommerce.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessError {

    /**
     * 사용자
     */
    INVALID_USER_ID("U101", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID입니다."),
    USER_NOT_FOUND("U001", HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),

    /**
     * 포인트
     */
    INVALID_POINT_TRANSACTION_ID("PT101", HttpStatus.BAD_REQUEST, "유효하지 않은 포인트 거래내역 ID입니다."),
    INSUFFICIENT_POINT("P101", HttpStatus.BAD_REQUEST, "잔액이 부족합니다."), // TODO CONFLICT 도 고려해보기

    /**
     * 쿠폰
     */
    COUPON_POLICY_NOT_FOUND("CP001", HttpStatus.NOT_FOUND, "쿠폰 정책이 존재하지 않습니다."),
    COUPON_POLICY_UNAVAILABLE("CP002", HttpStatus.BAD_REQUEST, "발급할 수 없는 쿠폰입니다."),
    COUPON_POLICY_ALREADY_ISSUED("CP003", HttpStatus.BAD_REQUEST, "이미 발급받은 쿠폰입니다."),

    USER_COUPON_NOT_FOUND("UC004", HttpStatus.NOT_FOUND, "사용자 쿠폰이 존재하지 않습니다."),
    USER_COUPON_NOT_AVAILABLE("UC005", HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    USER_COUPON_EXPIRED("UC006", HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    USER_COUPON_ALREADY_USED("UC007", HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),


    /**
     * 상품
     */
    PRODUCT_NOT_FOUND("PR001", HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    PRODUCT_OPTION_NOT_FOUND("PR002", HttpStatus.NOT_FOUND, "상품 옵션이 존재하지 않습니다."),
    PRODUCT_OUT_OF_STOCK("PR003", HttpStatus.NOT_FOUND, "재고 ...."),

    /**
     * 주문
     */
    ORDER_NOT_FOUND("O001",  HttpStatus.NOT_FOUND, "주문 정보가 존재하지 않습니다."),

    /**
     * 결제
     */

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
