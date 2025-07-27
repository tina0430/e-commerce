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
    UNKNOWN_ERROR("SYS-999", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    /**
     * 파라미터 검증 오류
     */
    INVALID_PARAMETER("SYS-001", HttpStatus.BAD_REQUEST, "잘못된 파라미터 형식입니다."),
    INVALID_NUMBER_FORMAT("SYS-002", HttpStatus.BAD_REQUEST, "잘못된 숫자 형식입니다."),

    // todo
    INVALID_USER_ID("SYS-003", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID입니다."),
    INVALID_POINT_TRANSACTION_ID("SYS-004", HttpStatus.BAD_REQUEST, "유효하지 않은 포인트 거래내역 ID입니다."),
    INVALID_COUPON_POLICY_ID("SYS-005", HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 정책 ID입니다."),
    INVALID_USER_COUPON_ID("SYS-006", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 쿠폰 ID입니다."),
    INVALID_PRODUCT_ID("SYS-007", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다."),
    INVALID_PRODUCT_OPTION_ID("SYS-008", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 옵션 ID입니다."),
    INVALID_REQUESTED_QUANTITY("SYS-009", HttpStatus.BAD_REQUEST, "요청 수량은 1개 이상이어야 합니다."),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
