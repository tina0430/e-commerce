package kr.hhplus.be.ecommerce.coupon.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("쿠폰 컨트롤러 단위 테스트")
class CouponControllerUnitTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_COUPON_POLICY_ID = 10L;
    private static final Long TEST_USER_COUPON_ID = 100L;
    private static final String TEST_COUPON_NAME = "테스트쿠폰";
    private static final int TEST_DISCOUNT_VALUE = 1000;
    private static final int TEST_MIN_ORDER_AMOUNT = 5000;
    private static final int TEST_MAX_DISCOUNT_AMOUNT = 2000;
    private static final int TEST_TOTAL_QUANTITY = 100;
    private static final int TEST_VALID_DAYS = 30;
    private static final LocalDateTime TEST_ISSUE_START = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime TEST_ISSUE_END = LocalDateTime.of(2024, 12, 31, 23, 59);

    @Mock
    private CouponService couponService;

    @Mock
    private CouponDtoMapper couponMapper;

    @InjectMocks
    private CouponController couponController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("쿠폰 정책 생성")
    class CreateCouponPolicy {
        @Test
        @DisplayName("금액 쿠폰 정책 생성 성공")
        void createAmountCouponPolicy() throws Exception {
            // given
            CouponPolicyDto.CreateAmountCouponRequest request = new CouponPolicyDto.CreateAmountCouponRequest(
                    TEST_COUPON_NAME, TEST_DISCOUNT_VALUE, TEST_MIN_ORDER_AMOUNT, TEST_MAX_DISCOUNT_AMOUNT,
                    TEST_ISSUE_START, TEST_ISSUE_END, TEST_TOTAL_QUANTITY, TEST_VALID_DAYS
            );

            // when & then
            mockMvc.perform(post("/api/coupon-policies/amount")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.couponPolicyId").value(1L));
        }
    }

    @Nested
    @DisplayName("발급 가능 쿠폰 조회")
    class GetAvailableCoupons {
        @Test
        @DisplayName("사용자별 발급 가능 쿠폰 목록 조회 성공")
        void getAvailableCoupons() throws Exception {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .issueStartAt(TEST_ISSUE_START)
                    .issueEndAt(TEST_ISSUE_END)
                    .totalQuantity(TEST_TOTAL_QUANTITY)
                    .validDurationDays(TEST_VALID_DAYS)
                    .build();
            CouponPolicyDto.AvailableCoupon dto = new CouponPolicyDto.AvailableCoupon(
                    TEST_COUPON_POLICY_ID, TEST_COUPON_NAME, null, TEST_DISCOUNT_VALUE, TEST_MIN_ORDER_AMOUNT,
                    TEST_MAX_DISCOUNT_AMOUNT, TEST_ISSUE_START, TEST_ISSUE_END, TEST_TOTAL_QUANTITY, TEST_VALID_DAYS
            );
            when(couponService.getAvailableCoupons(TEST_USER_ID)).thenReturn(List.of(policy));
            when(couponMapper.toAvailableCouponDtoList(List.of(policy))).thenReturn(List.of(dto));

            // when & then
            mockMvc.perform(get("/api/users/{userId}/coupons/available", TEST_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].couponPolicyId").value(TEST_COUPON_POLICY_ID))
                    .andExpect(jsonPath("$[0].couponName").value(TEST_COUPON_NAME));
        }
    }

    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {
        @Test
        @DisplayName("사용자 쿠폰 발급 성공")
        void issueCoupon() throws Exception {
            // given
            UserCouponDto.IssueRequest request = new UserCouponDto.IssueRequest(TEST_USER_ID, TEST_COUPON_POLICY_ID);
            UserCoupon issuedCoupon = UserCoupon.builder().userCouponId(TEST_USER_COUPON_ID).build();
            UserCouponDto.IssueResponse response = new UserCouponDto.IssueResponse(TEST_USER_COUPON_ID);

            when(couponService.issueCoupon(TEST_USER_ID, TEST_COUPON_POLICY_ID)).thenReturn(issuedCoupon);
            when(couponMapper.toIssueResponseDto(issuedCoupon)).thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/coupons/issue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userCouponId").value(TEST_USER_COUPON_ID));
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 목록 조회")
    class GetUserCoupons {
        @Test
        @DisplayName("사용자 보유 쿠폰 목록 조회 성공")
        void getUserCoupons() throws Exception {
            // given
            UserCoupon userCoupon = UserCoupon.builder().userCouponId(TEST_USER_COUPON_ID).build();
            UserCouponDto.UserCoupon userCouponDto = new UserCouponDto.UserCoupon(
                    TEST_USER_COUPON_ID, TEST_USER_ID, TEST_COUPON_NAME, null, TEST_DISCOUNT_VALUE, TEST_MIN_ORDER_AMOUNT, TEST_ISSUE_END
            );

            when(couponService.getUserCoupons(TEST_USER_ID)).thenReturn(List.of(userCoupon));
            when(couponMapper.toUserCouponDtoList(List.of(userCoupon))).thenReturn(List.of(userCouponDto));

            // when & then
            mockMvc.perform(get("/api/users/{userId}/coupons", TEST_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.coupons[0].couponId").value(TEST_USER_COUPON_ID))
                    .andExpect(jsonPath("$.coupons[0].userId").value(TEST_USER_ID));
        }
    }
} 