package kr.hhplus.be.ecommerce.coupon.presentation;

import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private CouponResponseMapper couponMapper;

    @Test
    @DisplayName("발급 가능한 쿠폰 목록을 조회한다")
    void getAvailableCoupons() throws Exception {
        // given
        Long userId = 1L;
        List<CouponPolicy> availableCoupons = List.of(
            CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponName("10% 할인 쿠폰")
                .discountType(DiscountType.RATE)
                .discountValue(10L)
                .minOrderAmount(10000L)
                .maxDiscountAmount(5000L)
                .build()
        );

        List<CouponPolicyDto.AvailableCoupon> expectedResponse = List.of(
            new CouponPolicyDto.AvailableCoupon(
                1L, "10% 할인 쿠폰", DiscountType.RATE, 10, 10000, 5000,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 100, 30
            )
        );

        when(couponService.getAvailableCoupons(userId)).thenReturn(availableCoupons);
        when(couponMapper.toAvailableCouponDtoList(availableCoupons)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/users/{userId}/coupons/available", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponPolicyId").value(1))
                .andExpect(jsonPath("$[0].couponName").value("10% 할인 쿠폰"))
                .andExpect(jsonPath("$[0].discountType").value("RATE"));
    }

    @Test
    @DisplayName("사용자의 쿠폰 목록을 조회한다")
    void getUserCoupons() throws Exception {
        // given
        Long userId = 1L;
        List<UserCoupon> userCoupons = List.of(
            UserCoupon.builder()
                .userCouponId(1L)
                .userId(userId)
                .couponPolicyId(1L)
                .couponName("10% 할인 쿠폰")
                .build()
        );

        List<UserCouponDto.UserCoupon> expectedDtos = List.of(
            new UserCouponDto.UserCoupon(1L, userId, "10% 할인 쿠폰", 
                DiscountType.RATE, 10, 10000, LocalDateTime.now().plusDays(30))
        );

        when(couponService.getUserCoupons(userId)).thenReturn(userCoupons);
        when(couponMapper.toUserCouponDtoList(userCoupons)).thenReturn(expectedDtos);

        // when & then
        mockMvc.perform(get("/api/users/{userId}/coupons", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupons[0].couponId").value(1))
                .andExpect(jsonPath("$.coupons[0].couponName").value("10% 할인 쿠폰"));
    }

} 