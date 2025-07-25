package kr.hhplus.be.ecommerce.user.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 유저 정보(포인트 잔액(balance) 포함)을 나타내는 순수한 도메인 객체
 * TODO 요구되는 유저 정보가 많아지면 UserPoint로 분리 예정
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;
    private String userName;
    private Long balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public boolean hasEnoughPoint(long amount) {
        return this.balance >= amount;
    }

    public void chargePoint(long amount) {
        this.balance += amount;
    }

    public void usePoint(long amount) {
        if (!hasEnoughPoint(amount)) {
            throw new BusinessException(BusinessError.INSUFFICIENT_POINT, "현재 잔액: " + this.balance + ", 필요 금액: " + amount);
        }
        this.balance -= amount;
    }

} 