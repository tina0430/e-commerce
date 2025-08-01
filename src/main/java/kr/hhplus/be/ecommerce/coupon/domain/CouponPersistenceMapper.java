package kr.hhplus.be.ecommerce.coupon.domain;

import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicyEntity;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponPersistenceMapper {

    /**
     * 쿠폰 정책
     */
    CouponPolicy toCouponPolicy(CouponPolicyEntity source);

    List<CouponPolicy> toCouponPolicyList(List<CouponPolicyEntity> source);

    /**
     * 사용자 쿠폰
     */
    UserCoupon toUserCoupon(UserCouponEntity userCouponEntity);

    List<UserCoupon> toUserCouponList(List<UserCouponEntity> source);

    UserCouponEntity toUserCouponEntity(UserCoupon userCoupon);

    /**
     * 도메인 객체의 변경된 상태를 기존 엔티티에 적용합니다.
     * <p>
     * 이 메서드는 새로운 엔티티를 생성하지 않으며,
     * 전달받은 엔티티 객체의 상태만 변경합니다.
     *
     * @param domain 도메인 객체
     * @param entity 기존 엔티티 (상태가 변경됨)
     */
    default void applyToEntity(UserCoupon domain, UserCouponEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setCouponName(domain.getCouponName());
        entity.setDiscountType(domain.getDiscountType());
        entity.setDiscountValue(domain.getDiscountValue());
        entity.setMaxDiscountAmount(domain.getMaxDiscountAmount());
        entity.setMinOrderAmount(domain.getMinOrderAmount());
        entity.setUsageStatus(domain.getUsageStatus());
        entity.setStartAt(domain.getStartAt());
        entity.setEndAt(domain.getEndAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    /**
     * 도메인 객체의 변경된 상태를 기존 엔티티에 적용합니다.
     * <p>
     * 이 메서드는 새로운 엔티티를 생성하지 않으며,
     * 전달받은 엔티티 객체의 상태만 변경합니다.
     *
     * @param domain 도메인 객체
     * @param entity 기존 엔티티 (상태가 변경됨)
     */
    default void applyToEntity(CouponPolicy domain, CouponPolicyEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setCouponStatus(domain.getCouponStatus());
        entity.setTotalQuantity(domain.getTotalQuantity());
        entity.setRemainingQuantity(domain.getRemainingQuantity());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}