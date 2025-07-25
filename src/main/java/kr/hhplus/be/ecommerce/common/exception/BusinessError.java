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
    UNKNOWN_ERROR("COM-000", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    /**
     * 사용자
     */
    INVALID_USER_ID("USER-101", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID입니다."),
    USER_NOT_FOUND("USER-201", HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),

    /**
     * 포인트
     */
    INVALID_POINT_TRANSACTION_ID("POINT-101", HttpStatus.BAD_REQUEST, "유효하지 않은 포인트 거래내역 ID입니다."),
    INSUFFICIENT_POINT("POINT-102", HttpStatus.BAD_REQUEST, "잔액이 부족합니다."), // TODO: CONFLICT도 고려해보기

    /**
     * 쿠폰 정책
     */
    INVALID_COUPON_POLICY_ID("POLICY-101", HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 정책 ID입니다."),
    COUPON_POLICY_NOT_FOUND("POLICY-201", HttpStatus.NOT_FOUND, "쿠폰 정책이 존재하지 않습니다."),
    COUPON_POLICY_UNAVAILABLE("POLICY-301", HttpStatus.BAD_REQUEST, "발급할 수 없는 쿠폰입니다."),
    COUPON_POLICY_ALREADY_ISSUED("POLICY-302", HttpStatus.BAD_REQUEST, "이미 발급받은 쿠폰입니다."),

    /**
     * 사용자 쿠폰
     */
    INVALID_USER_COUPON_ID("COUPON-101", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 쿠폰 ID입니다."),
    USER_COUPON_NOT_FOUND("COUPON-201", HttpStatus.NOT_FOUND, "사용자 쿠폰이 존재하지 않습니다."),
    USER_COUPON_NOT_AVAILABLE("COUPON-301", HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    USER_COUPON_EXPIRED("COUPON-302", HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    USER_COUPON_ALREADY_USED("COUPON-303", HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    UNAUTHORIZED_USER_COUPON_ACCESS("COUPON-401", HttpStatus.FORBIDDEN, "해당 쿠폰에 접근할 권한이 없습니다."),

    /**
     * 상품
     */
    INVALID_PRODUCT_ID("PRODUCT-101", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다."),
    PRODUCT_NOT_FOUND("PRODUCT-201", HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),

    INVALID_PRODUCT_OPTION_ID("PRODUCT_OPTION-101", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 옵션 ID입니다."),
    INVALID_REQUESTED_QUANTITY("PRODUCT_OPTION-102", HttpStatus.BAD_REQUEST, "요청 수량은 1개 이상이어야 합니다."),
    PRODUCT_OPTION_NOT_FOUND("PRODUCT_OPTION-201", HttpStatus.NOT_FOUND, "해당 상품 옵션이 존재하지 않습니다."),
    PRODUCT_OPTION_OUT_OF_STOCK("PRODUCT_OPTION-202", HttpStatus.BAD_REQUEST, "해당 상품 옵션의 재고가 부족합니다."),

    /**
     * 주문
     */
    ORDER_NOT_FOUND("ORDER-201", HttpStatus.NOT_FOUND, "해당 주문 정보가 존재하지 않습니다."),
    INVALID_DISCOUNT_AMOUNT("ORDER-101", HttpStatus.BAD_REQUEST, "할인 금액이 상품 가격보다 클 수 없습니다."),
    UNAUTHORIZED_ORDER_ACCESS("ORDER-401", HttpStatus.FORBIDDEN, "해당 주문에 접근할 권한이 없습니다."),

    /**
     * 결제
     */
    PAYMENT_NOT_FOUND("PAYMENT-201", HttpStatus.NOT_FOUND, "결제 정보가 존재하지 않습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
