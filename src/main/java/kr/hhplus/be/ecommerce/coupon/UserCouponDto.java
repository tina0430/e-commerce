package kr.hhplus.be.ecommerce.coupon;

import java.time.LocalDateTime;
import java.util.List;

public class UserCouponDto {

    // C-1 쿠폰 정책 발행 요청
    public record CreateRequest(String couponName,
                                DiscountType discountType,
                                Integer discountValue,
                                Integer minOrderAmount,
                                Integer minOrderPrice,
                                LocalDateTime issueStartAt,
                                LocalDateTime issueEndAt,
                                Integer totalQuantity,
                                Integer validDurationDays,
                                CouponPolicyStatus status) {
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
                                  Integer minOrderPrice,
                                  LocalDateTime issueEndAt) {

    }

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
                                  Integer minOrderPrice,
                                  LocalDateTime issueEndAt) {

    }

    // C-4 사용자 쿠폰 목록 조회 응답
    public record UserCouponListResponse(List<UserCoupon> coupons) {
    }

    // C-5 쿠폰 사용 요청
    public record UseRequest(Long userId, Long couponId) {
    }

    // C-5 쿠폰 사용 응답
    public record UseResponse(Long userCouponId) {
    }
}