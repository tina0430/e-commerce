package kr.hhplus.be.ecommerce.coupon.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CouponPolicy 도메인 객체
 * 쿠폰 정책 정보를 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponPolicy {

    private Long couponPolicyId;
    private String couponName;
    private DiscountType discountType;
    private Long discountValue;
    private Long maxDiscountAmount;
    private Long minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Integer validDurationDays;
    private CouponPolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 쿠폰 발급 가능 여부 확인
     * @return 발급 가능 여부
     */
    public boolean isAvailableForIssue() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == CouponPolicyStatus.ACTIVE &&
               now.isAfter(this.issueStartAt) &&
               now.isBefore(this.issueEndAt) &&
               this.remainingQuantity > 0;
    }

    /**
     * 쿠폰 발급 처리
     */
    public void issue() {
        if (!isAvailableForIssue()) {
            throw new IllegalArgumentException("발급할 수 없는 쿠폰입니다.");
        }
        this.remainingQuantity--;
        
        if (this.remainingQuantity == 0) {
            this.status = CouponPolicyStatus.ENDED;
        }
    }

}
