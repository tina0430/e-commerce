package kr.hhplus.be.ecommerce.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 거래 내역을 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointTransaction {

    private Long transactionId;
    private Long userId;
    private TransactionType transactionType;
    private Integer amount;
    private Integer balance;
    private LocalDateTime createdAt;

} 