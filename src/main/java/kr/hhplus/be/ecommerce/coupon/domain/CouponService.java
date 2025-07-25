package kr.hhplus.be.ecommerce.coupon.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyEntity;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPersistenceMapper couponMapper;

    public List<UserCoupon> getUserCoupons(Long userId) {
        List<UserCouponEntity> userCouponEntities = couponRepository.findCouponsByUserId(userId);
        return couponMapper.toUserCouponList(userCouponEntities);
    }

    public List<CouponPolicy> getAvailableCoupons(Long userId) {
        Set<Long> alreadyIssued = couponRepository.findCouponsByUserId(userId).stream()
                .map(UserCouponEntity::getCouponPolicyId)
                .collect(Collectors.toSet());
        List<CouponPolicyEntity> availableToIssue = couponRepository.findAvailablePolicies();
        List<CouponPolicyEntity> filtered = availableToIssue.stream()
                .filter(p -> !alreadyIssued.contains(p.getCouponPolicyId()))
                .toList();
        return couponMapper.toCouponPolicyList(filtered);
    }

    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponPolicyId) {
        CouponPolicy couponPolicy = getValidCouponPolicy(couponPolicyId);
        validateNotAlreadyIssued(userId, couponPolicy);
        UserCoupon userCoupon = UserCoupon.issue(userId, couponPolicy);
        UserCouponEntity savedEntity = couponRepository.save(couponMapper.toUserCouponEntity(userCoupon));
        return couponMapper.toUserCoupon(savedEntity);
    }

    private CouponPolicy getValidCouponPolicy(Long couponPolicyId) {
        CouponPolicyEntity entity = couponRepository.findPolicyById(couponPolicyId)
                .orElseThrow(() -> new BusinessException(BusinessError.COUPON_POLICY_NOT_FOUND));
        CouponPolicy policy = couponMapper.toCouponPolicy(entity);
        if (!policy.isAvailableForIssue()) {
            throw new BusinessException(BusinessError.COUPON_POLICY_UNAVAILABLE);
        }
        return policy;
    }

    private void validateNotAlreadyIssued(Long userId, CouponPolicy couponPolicy) {
        List<UserCouponEntity> issuedEntities = couponRepository.findCouponsByUserId(userId);
        boolean alreadyIssued = issuedEntities.stream()
                .map(couponMapper::toUserCoupon)
                .anyMatch(c -> c.isIssuedFrom(couponPolicy));
        if (alreadyIssued) {
            throw new BusinessException(BusinessError.COUPON_POLICY_ALREADY_ISSUED);
        }
    }

    /**
     * 쿠폰 유효성 검증
     *
     * @param userCouponId 쿠폰 ID
     * @param userId       사용자 ID
     */
    public void validateCoupon(Long userCouponId, Long userId) {
        UserCouponEntity userCouponEntity = couponRepository.findCouponById(userCouponId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_COUPON_NOT_FOUND));
        UserCoupon userCoupon = couponMapper.toUserCoupon(userCouponEntity);
        if (!userCoupon.isValidForUser(userId)) {
            throw new BusinessException(BusinessError.UNAUTHORIZED_USER_COUPON_ACCESS);
        }
    }

    /**
     * 쿠폰 사용 처리
     *
     * @param userCouponId 쿠폰 ID
     * @param userId       사용자 ID
     */
    public UserCoupon useUserCoupon(Long userCouponId, Long userId) {
        UserCouponEntity userCouponEntity = couponRepository.findCouponById(userCouponId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_COUPON_NOT_FOUND));
        UserCoupon userCoupon = couponMapper.toUserCoupon(userCouponEntity);
        if (!userCoupon.isValidForUser(userId)) {
            throw new BusinessException(BusinessError.USER_COUPON_NOT_AVAILABLE);
        }
        userCoupon.use();
        // fixme 흠 뭔가 구림
        couponMapper.applyToEntity(userCoupon, userCouponEntity);
        UserCouponEntity updatedEntity = couponMapper.toUserCouponEntity(userCoupon);
        return couponMapper.toUserCoupon(couponRepository.save(updatedEntity));
    }

    /**
     * 쿠폰 상태 복원 (사용 완료 → 사용 가능)
     *
     * @param userCouponId 쿠폰 ID
     * @param userId       사용자 ID
     */
    public void restoreUserCoupon(Long userCouponId, Long userId) {
        UserCouponEntity userCouponEntity = couponRepository.findCouponById(userCouponId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_COUPON_NOT_FOUND));
        UserCoupon userCoupon = couponMapper.toUserCoupon(userCouponEntity);
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BusinessException(BusinessError.USER_COUPON_NOT_AVAILABLE);
        }
        userCoupon.restore();
        couponMapper.applyToEntity(userCoupon, userCouponEntity);
    }

}
