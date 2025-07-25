package kr.hhplus.be.ecommerce.coupon.presentation;

import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponDtoMapper {

    /**
     * 쿠폰 정책
     */
    CouponPolicyDto.AvailableCoupon toAvailableCouponDto(CouponPolicy source);

    List<CouponPolicyDto.AvailableCoupon> toAvailableCouponDtoList(List<CouponPolicy> source);

    /**
     * 사용자 쿠폰
     */
    UserCouponDto.IssueResponse toIssueResponseDto(UserCoupon source);

    @Mapping(target = "couponId", source = "userCouponId")
    UserCouponDto.UserCoupon toUserCouponDto(UserCoupon source);

    List<UserCouponDto.UserCoupon> toUserCouponDtoList(List<UserCoupon> source);
}