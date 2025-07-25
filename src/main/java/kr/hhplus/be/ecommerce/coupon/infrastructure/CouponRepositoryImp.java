package kr.hhplus.be.ecommerce.coupon.infrastructure;

import kr.hhplus.be.ecommerce.coupon.domain.CouponRepository;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyEntity;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImp implements CouponRepository {

    private final JpaCouponRepository jpaCouponRepository;

    @Override
    public List<CouponPolicyEntity> findAvailablePolicies() {
        return jpaCouponRepository.findAvailableCouponPolicies();
    }

    @Override
    public Optional<UserCouponEntity> findCouponById(Long couponId) {
        return jpaCouponRepository.findCouponById(couponId);
    }

    @Override
    public List<UserCouponEntity> findCouponsByUserId(Long userId) {
        return jpaCouponRepository.findCouponsByUserId(userId);
    }

    @Override
    public UserCouponEntity save(UserCouponEntity userCoupon) {
        return jpaCouponRepository.saveUserCoupon(userCoupon);
    }

}
