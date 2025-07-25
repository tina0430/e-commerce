package kr.hhplus.be.ecommerce.common.exception;

import kr.hhplus.be.ecommerce.common.exception.base.ErrorBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessError implements ErrorBase {

    /**
     * 공통
     */
    UNKNOWN_ERROR("CO000", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

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
    INVALID_COUPON_POLICY_ID("CP101", HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 정책 ID입니다."),

    COUPON_POLICY_NOT_FOUND("CP201", HttpStatus.NOT_FOUND, "쿠폰 정책이 존재하지 않습니다."),

    COUPON_POLICY_UNAVAILABLE("CP301", HttpStatus.BAD_REQUEST, "발급할 수 없는 쿠폰입니다."),
    COUPON_POLICY_ALREADY_ISSUED("CP302", HttpStatus.BAD_REQUEST, "이미 발급받은 쿠폰입니다."),

    INVALID_USER_COUPON_ID("UC101", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 쿠폰 ID입니다."),

    USER_COUPON_NOT_FOUND("UC201", HttpStatus.NOT_FOUND, "사용자 쿠폰이 존재하지 않습니다."),

    USER_COUPON_NOT_AVAILABLE("UC301", HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    USER_COUPON_EXPIRED("UC302", HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    USER_COUPON_ALREADY_USED("UC303", HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),


    /**
     * 상품
     */
    INVALID_PRODUCT_ID("PR101", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다."),

    PRODUCT_NOT_FOUND("PR201", HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),

    INVALID_PRODUCT_OPTION_ID("PO101", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 옵션 ID입니다."),
    INVALID_REQUESTED_QUANTITY("PO102", HttpStatus.BAD_REQUEST, "요청 수량은 1개 이상이어야 합니다."),

    PRODUCT_OPTION_NOT_FOUND("PO201", HttpStatus.NOT_FOUND, "해당 상품 옵션이 존재하지 않습니다."),
    PRODUCT_OPTION_OUT_OF_STOCK("PO202", HttpStatus.NOT_FOUND, "해당 상품 옵션의 재고가 부족합니다."),

    /**
     * 주문
     */
    ORDER_NOT_FOUND("O2001",  HttpStatus.NOT_FOUND, "해당 주문 정보가 존재하지 않습니다."),

    /**
     * 결제
     */

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
