package kr.hhplus.be.ecommerce.common.exception;

public class ErrorResponse {

    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // getter만 있으면 JSON 직렬화 가능
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
