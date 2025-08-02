package kr.hhplus.be.ecommerce.coupon.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.ecommerce.common.infrastructure.JpaRepositoryBase;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyEntity;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyStatus;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponEntity;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.coupon.domain.model.QCouponPolicyEntity.couponPolicyEntity;
import static kr.hhplus.be.ecommerce.coupon.domain.model.QUserCouponEntity.userCouponEntity;

@Repository
public class JpaCouponRepository extends JpaRepositoryBase {

    public JpaCouponRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    /**
     * 쿠폰 정책
     */
    public Optional<CouponPolicyEntity> findPolicyById(Long policyId) {
        return Optional.ofNullable(entityManager.find(CouponPolicyEntity.class, policyId));
    }

    public List<CouponPolicyEntity> findAvailableCouponPolicies() {
        return queryFactory
                .selectFrom(couponPolicyEntity)
                .where(couponPolicyEntity.couponStatus.eq(CouponPolicyStatus.ACTIVE))
                .fetch();
    }

    public CouponPolicyEntity saveCouponPolicy(CouponPolicyEntity couponPolicyEntity) {
        return super.save(couponPolicyEntity);
    }

    /**
     * 사용자 쿠폰
     */
    public Optional<UserCouponEntity> findCouponById(Long couponId) {
        return Optional.ofNullable(entityManager.find(UserCouponEntity.class, couponId));
    }

    public List<UserCouponEntity> findCouponsByUserId(Long userId) {
        return queryFactory
                .selectFrom(userCouponEntity)
                .where(userCouponEntity.userId.eq(userId))
                .fetch();
    }

    public UserCouponEntity saveUserCoupon(UserCouponEntity userCoupon) {
        return super.save(userCoupon);
    }
}