package kr.hhplus.be.ecommerce.point;

import java.time.LocalDateTime;

public class PointDto {

    // B-1 포인트 조회 및 잔액 충전/사용 응답
    public record Response(Long userId, Long balance) {}

    // B-2 포인트 충전 요청
    public record ChargeRequest(Long userId, Long amount) {}

    // B-3 포인트 사용 요청
    public record UseRequest(Long userId, Long amount) {}

    // B-4 포인트 내역 조회 응답
    public record HistoryResponse(Long transactionId,
                                  Long userId,
                                  TransactionType transactionType,
                                  Integer amount,
                                  Integer balance,
                                  LocalDateTime createdAt) {}
}
