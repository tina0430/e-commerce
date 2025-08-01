package kr.hhplus.be.ecommerce.coupon.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("쿠폰 정책 도메인 모델 테스트")
class CouponPolicyTest {

    private static final Long TEST_COUPON_POLICY_ID = 1L;
    private static final String TEST_COUPON_NAME = "테스트 쿠폰";
    private static final DiscountType TEST_DISCOUNT_TYPE = DiscountType.AMOUNT;
    private static final Integer TEST_DISCOUNT_VALUE = 1000;
    private static final Integer TEST_MAX_DISCOUNT_AMOUNT = 2000;
    private static final Integer TEST_MIN_ORDER_AMOUNT = 5000;
    private static final Integer TEST_TOTAL_QUANTITY = 100;
    private static final Integer TEST_VALID_DAYS = 30;
    private static final LocalDateTime TEST_ISSUE_START = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime TEST_ISSUE_END = LocalDateTime.of(2024, 12, 31, 23, 59);

    @Nested
    @DisplayName("쿠폰 정책 생성")
    class CreateCouponPolicy {
        @Test
        @DisplayName("쿠폰 정책을 생성한다")
        void createCouponPolicy() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .issueStartAt(TEST_ISSUE_START)
                    .issueEndAt(TEST_ISSUE_END)
                    .totalQuantity(TEST_TOTAL_QUANTITY)
                    .remainingQuantity(TEST_TOTAL_QUANTITY)
                    .validDurationDays(TEST_VALID_DAYS)
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .build();

            // when & then
            assertThat(policy.getCouponPolicyId()).isEqualTo(TEST_COUPON_POLICY_ID);
            assertThat(policy.getCouponName()).isEqualTo(TEST_COUPON_NAME);
            assertThat(policy.getDiscountType()).isEqualTo(TEST_DISCOUNT_TYPE);
            assertThat(policy.getDiscountValue()).isEqualTo(TEST_DISCOUNT_VALUE);
            assertThat(policy.getMaxDiscountAmount()).isEqualTo(TEST_MAX_DISCOUNT_AMOUNT);
            assertThat(policy.getMinOrderAmount()).isEqualTo(TEST_MIN_ORDER_AMOUNT);
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 가능 여부 확인")
    class IsAvailableForIssue {
        @Test
        @DisplayName("발급 가능한 쿠폰은 true를 반환한다")
        void isAvailableForIssue_WhenAvailable() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .remainingQuantity(10)
                    .build();

            // when
            boolean result = policy.isAvailableForIssue();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("비활성 상태인 쿠폰은 false를 반환한다")
        void isAvailableForIssue_WhenInactive() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponStatus(CouponPolicyStatus.PENDING)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .remainingQuantity(10)
                    .build();

            // when
            boolean result = policy.isAvailableForIssue();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("발급 기간이 지나지 않은 쿠폰은 false를 반환한다")
        void isAvailableForIssue_WhenBeforeIssueStart() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().plusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(2))
                    .remainingQuantity(10)
                    .build();

            // when
            boolean result = policy.isAvailableForIssue();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("발급 기간이 만료된 쿠폰은 false를 반환한다")
        void isAvailableForIssue_WhenAfterIssueEnd() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(2))
                    .issueEndAt(LocalDateTime.now().minusDays(1))
                    .remainingQuantity(10)
                    .build();

            // when
            boolean result = policy.isAvailableForIssue();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("재고가 없는 쿠폰은 false를 반환한다")
        void isAvailableForIssue_WhenNoRemainingQuantity() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .remainingQuantity(0)
                    .build();

            // when
            boolean result = policy.isAvailableForIssue();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 처리")
    class Issue {
        @Test
        @DisplayName("발급 가능한 쿠폰을 발급한다")
        void issue_WhenAvailable() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .validDurationDays(TEST_VALID_DAYS)
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .totalQuantity(TEST_TOTAL_QUANTITY)
                    .remainingQuantity(TEST_TOTAL_QUANTITY)
                    .build();

            List<UserCoupon> issuedCoupons = new ArrayList<>();

            // when
            policy.issue(issuedCoupons);

            // then
            assertThat(policy.getRemainingQuantity()).isEqualTo(TEST_TOTAL_QUANTITY - 1);
        }

        @Test
        @DisplayName("이미 발급받은 쿠폰을 다시 발급하려 하면 예외가 발생한다")
        void issue_WhenAlreadyIssued() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .validDurationDays(TEST_VALID_DAYS)
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .remainingQuantity(10)
                    .build();

            List<UserCoupon> issuedCoupons = new ArrayList<>();
            
            // 첫 번째 발급
            policy.issue(issuedCoupons);
            
            // when & then
            assertThatThrownBy(() -> policy.issue(issuedCoupons))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.COUPON_POLICY_ALREADY_ISSUED.getCode());
                    });
        }

        @Test
        @DisplayName("마지막 쿠폰 발급 시 상태가 ENDED로 변경된다")
        void issue_WhenLastCoupon() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .validDurationDays(TEST_VALID_DAYS)
                    .couponStatus(CouponPolicyStatus.ACTIVE)
                    .issueStartAt(LocalDateTime.now().minusDays(1))
                    .issueEndAt(LocalDateTime.now().plusDays(1))
                    .remainingQuantity(1)
                    .build();

            List<UserCoupon> issuedCoupons = new ArrayList<>();

            // when
            policy.issue(issuedCoupons);

            // then
            assertThat(policy.getRemainingQuantity()).isEqualTo(0);
            assertThat(policy.getCouponStatus()).isEqualTo(CouponPolicyStatus.ENDED);
        }
    }

    @Nested
    @DisplayName("최대 할인 금액 반환")
    class GetMaxDiscountAmount {
        @Test
        @DisplayName("최대 할인 금액을 반환한다")
        void getMaxDiscountAmount() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .build();

            // when & then
            assertThat(policy.getMaxDiscountAmount()).isEqualTo(TEST_MAX_DISCOUNT_AMOUNT);
        }
    }
} 