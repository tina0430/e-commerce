package kr.hhplus.be.ecommerce.coupon.domain;

import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyEntity;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponEntity;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    /**
     * 쿠폰 정책
     */
    List<CouponPolicyEntity> findAvailablePolicies();

    /**
     * 사용자 쿠폰
     */
    Optional<UserCouponEntity> findCouponById(Long couponId);
    List<UserCouponEntity> findCouponsByUserId(Long userId);
    UserCouponEntity save(UserCouponEntity userCoupon);

}
