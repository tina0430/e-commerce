package kr.hhplus.be.ecommerce.user.presentation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.ecommerce.user.domain.model.TransactionType;

import java.time.LocalDateTime;

public class PointDto {

    // B-1 포인트 조회 및 잔액 충전/사용 응답
    public record Response(Long userId, Long balance) {}

    // B-2 포인트 충전 요청
    public record ChargeRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            @Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.")
            Long userId,

            @NotNull(message = "충전 금액은 필수입니다.")
            @Min(value = 1000, message = "최소 충전 금액은 1,000원 입니다.")
            // TODO 단위 체크
            Long amount) {}

    // B-3 포인트 사용 요청
    public record UseRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            @Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.")
            Long userId,

            @NotNull(message = "사용 금액은 필수입니다.")
            @Min(value = 100, message = "최소 사용 금액은 100원 입니다.")
            @Max(value = 1_000_000, message = "1회 최대 사용 금액은 1,000,000원 입니다.")
            // TODO 단위 체크
            Long amount) {}

    // B-4 포인트 내역 조회 응답
    public record HistoryResponse(Long transactionId,
                                  Long userId,
                                  TransactionType transactionType,
                                  Long amount,
                                  Long balance,
                                  LocalDateTime createdAt) {}
}
