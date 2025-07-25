package kr.hhplus.be.ecommerce.coupon.presentation;

import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;

import java.time.LocalDateTime;
import java.util.List;

public class UserCouponDto {

    // C-3 사용자 쿠폰 발급 요청
    public record IssueRequest(Long userId, Long couponPolicyId) {

    }

    // C-3 사용자 쿠폰 발급 응답
    public record IssueResponse(Long userCouponId) {
    }

    // C-4 사용자 쿠폰 조회
    public record UserCoupon(Long couponId,
                                  Long userId,
                                  String couponName,
                                  DiscountType discountType,
                                  Integer discountValue,
                                  Integer minOrderAmount,
                                  LocalDateTime endAt) {

    }

    // C-4 사용자 쿠폰 목록 조회 응답
    public record UserCouponListResponse(List<UserCoupon> coupons) {
    }

}