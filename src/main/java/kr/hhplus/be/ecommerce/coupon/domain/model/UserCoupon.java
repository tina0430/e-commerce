package kr.hhplus.be.ecommerce.coupon.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserCoupon 도메인 객체
 * 사용자의 쿠폰 정보를 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    private Long userCouponId;
    private Long couponPolicyId;
    private Long userId;
    private String couponName;
    private DiscountType discountType;
    private Long discountValue;
    private Long maxDiscountAmount;
    private Long minOrderAmount;
    private UserCouponStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 상태 확인
    /**
     * 쿠폰 사용 가능 여부 확인
     * @return 사용 가능 여부
     */
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        if (status == UserCouponStatus.EXPIRED || now.isAfter(this.endAt)) {
            throw new BusinessException(BusinessError.USER_COUPON_EXPIRED);
        }
        if (status == UserCouponStatus.USED) {
            throw new BusinessException(BusinessError.USER_COUPON_ALREADY_USED);
        }
        if (now.isBefore(this.startAt)) {
            throw new BusinessException(BusinessError.USER_COUPON_ALREADY_USED);
        }
        return this.status == UserCouponStatus.AVAILABLE &&
               now.isAfter(this.startAt) && 
               now.isBefore(this.endAt);
    }

    /**
     * 쿠폰 만료 여부 확인
     * @return 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.endAt);
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isIssuedFrom(CouponPolicy policy) {
        return this.couponPolicyId.equals(policy.getCouponPolicyId());
    }

    public boolean isValidForUser(Long userId) {
        if (userId == null || !userId.equals(this.userId)) {
            return false;
        }
        return isAvailable();
    }

    /**
     * 쿠폰 사용 처리
     */
    public void use() {
        if (isAvailable()) {
            this.status = UserCouponStatus.USED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 쿠폰 만료 처리
     */
    public void expire() {
        this.status = UserCouponStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 쿠폰 상태 복원 (사용 완료 → 사용 가능)
     */
    public void restore() {
        if (this.status == UserCouponStatus.USED) {
            this.status = UserCouponStatus.AVAILABLE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // 상태 변경
    /**
     * 쿠폰 생성 팩토리 메서드
     * @param userId 사용자 ID
     * @param couponPolicy 쿠폰 정책
     * @return 생성된 쿠폰
     */
    public static UserCoupon issue(Long userId, CouponPolicy couponPolicy) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = now.plusDays(couponPolicy.getValidDurationDays());

        return UserCoupon.builder()
                .userId(userId)
                .couponPolicyId(couponPolicy.getCouponPolicyId())
                .minOrderAmount(couponPolicy.getMinOrderAmount())
                .maxDiscountAmount(couponPolicy.getMaxDiscountAmount())
                .discountType(couponPolicy.getDiscountType())
                .discountValue(couponPolicy.getDiscountValue())
                .status(UserCouponStatus.AVAILABLE)
                .startAt(now)
                .endAt(endAt)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * 쿠폰 할인 금액 계산
     * @param orderAmount 주문 금액
     * @return 할인 금액
     */
    public long calculateDiscountAmount(int orderAmount) {
        if (!isAvailable()) {
            return 0;
        }
        if (orderAmount < minOrderAmount) {
            return 0;
        }
        long discountAmount;
        if (discountType == DiscountType.RATE) {
            discountAmount = (orderAmount * discountValue) / 100;
        } else {
            discountAmount = maxDiscountAmount;
        }
        if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
            discountAmount = maxDiscountAmount;
        }
        return discountAmount;
    }

}