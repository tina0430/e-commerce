package kr.hhplus.be.ecommerce.coupon.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private Integer discountValue;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Integer validDurationDays;
    private CouponPolicyStatus couponStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 쿠폰 발급 가능 여부 확인
     * @return 발급 가능 여부
     */
    public boolean isAvailableForIssue() {
        LocalDateTime now = LocalDateTime.now();
        return this.couponStatus == CouponPolicyStatus.ACTIVE &&
               now.isAfter(this.issueStartAt) &&
               now.isBefore(this.issueEndAt) &&
               this.remainingQuantity > 0;
    }

    /**
     * 쿠폰 발급 처리
     */
    public void issue(List<UserCoupon> issuedCoupon) {
        boolean alreadyIssued = issuedCoupon.stream().anyMatch(c -> c.isIssuedFrom(this));
        if (alreadyIssued) {
            throw new BusinessException(BusinessError.COUPON_POLICY_ALREADY_ISSUED);
        }
        this.remainingQuantity--;
        if (this.remainingQuantity == 0) {
            this.couponStatus = CouponPolicyStatus.ENDED;
        }
    }

}
