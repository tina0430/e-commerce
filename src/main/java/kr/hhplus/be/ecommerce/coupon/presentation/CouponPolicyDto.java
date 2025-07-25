package kr.hhplus.be.ecommerce.coupon.presentation;

import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;

import java.time.LocalDateTime;

public class CouponPolicyDto {

    public sealed interface CreateCouponPolicyRequest
            permits CreateAmountCouponRequest, CreateRateCouponRequest {
    }

    // C-1 쿠폰 정책 발행 요청
    public record CreateAmountCouponRequest(String couponName,
                                Integer discountValue,
                                Integer minOrderAmount,
                                Integer maxDiscountAmount,
                                LocalDateTime issueStartAt,
                                LocalDateTime issueEndAt,
                                Integer totalQuantity,
                                Integer validDurationDays) implements CreateCouponPolicyRequest {
    }

    public record CreateRateCouponRequest(String couponName,
                                          Integer discountValue,
                                          Integer minOrderAmount,
                                          Integer maxDiscountAmount,
                                          LocalDateTime issueStartAt,
                                          LocalDateTime issueEndAt,
                                          Integer totalQuantity,
                                          Integer validDurationDays) implements CreateCouponPolicyRequest {
    }

    // C-1 쿠폰 정책 발행 응답
    public record CreateResponse(Long couponPolicyId) {
    }

    // C-2 발급 가능 쿠폰 조회
    public record AvailableCoupon(Long couponPolicyId,
                                  String couponName,
                                  DiscountType discountType,
                                  Integer discountValue,
                                  Integer minOrderAmount,
                                  Integer maxDiscountAmount,
                                  LocalDateTime issueStartAt,
                                  LocalDateTime issueEndAt,
                                  Integer totalQuantity,
                                  Integer validDurationDays) {
    }
}
