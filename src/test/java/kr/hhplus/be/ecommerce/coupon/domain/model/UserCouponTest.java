package kr.hhplus.be.ecommerce.coupon.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("사용자 쿠폰 도메인 모델 테스트")
class UserCouponTest {

    private static final Long TEST_USER_COUPON_ID = 1L;
    private static final Long TEST_COUPON_POLICY_ID = 10L;
    private static final Long TEST_USER_ID = 100L;
    private static final String TEST_COUPON_NAME = "테스트 쿠폰";
    private static final DiscountType TEST_DISCOUNT_TYPE = DiscountType.AMOUNT;
    private static final Integer TEST_DISCOUNT_AMOUNT = 1000;
    private static final Integer TEST_DISCOUNT_RATE = 10;
    private static final Integer TEST_MAX_DISCOUNT_AMOUNT = 1000;
    private static final Integer TEST_MIN_ORDER_AMOUNT = 5000;
    private static final Integer TEST_DURATION_DAYS = 7;
    private static final LocalDateTime TEST_START_AT = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime TEST_END_AT = LocalDateTime.of(2024, 12, 31, 23, 59);

    @Nested
    @DisplayName("사용자 쿠폰 생성")
    class CreateUserCoupon {
        @Test
        @DisplayName("사용자 쿠폰을 생성한다")
        void createUserCoupon() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .userCouponId(TEST_USER_COUPON_ID)
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .userId(TEST_USER_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_AMOUNT)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .startAt(TEST_START_AT)
                    .endAt(TEST_END_AT)
                    .build();

            // when & then
            assertThat(userCoupon.getUserCouponId()).isEqualTo(TEST_USER_COUPON_ID);
            assertThat(userCoupon.getCouponPolicyId()).isEqualTo(TEST_COUPON_POLICY_ID);
            assertThat(userCoupon.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(userCoupon.getCouponName()).isEqualTo(TEST_COUPON_NAME);
            assertThat(userCoupon.getDiscountType()).isEqualTo(TEST_DISCOUNT_TYPE);
            assertThat(userCoupon.getDiscountValue()).isEqualTo(TEST_DISCOUNT_AMOUNT);
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 팩토리 메서드")
    class Issue {
        @Test
        @DisplayName("쿠폰 정책으로부터 사용자 쿠폰을 발급한다")
        void issue() {
            // given
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_AMOUNT)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .validDurationDays(TEST_DURATION_DAYS)
                    .build();

            // when
            UserCoupon userCoupon = UserCoupon.issue(TEST_USER_ID, policy);

            // then
            assertThat(userCoupon.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(userCoupon.getCouponPolicyId()).isEqualTo(TEST_COUPON_POLICY_ID);
            assertThat(userCoupon.getCouponName()).isEqualTo(TEST_COUPON_NAME);
            assertThat(userCoupon.getDiscountType()).isEqualTo(TEST_DISCOUNT_TYPE);
            assertThat(userCoupon.getDiscountValue()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(userCoupon.getMaxDiscountAmount()).isEqualTo(TEST_MAX_DISCOUNT_AMOUNT);
            assertThat(userCoupon.getMinOrderAmount()).isEqualTo(TEST_MIN_ORDER_AMOUNT);
            assertThat(userCoupon.getUsageStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
            assertThat(userCoupon.getStartAt()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(userCoupon.getEndAt()).isAfter(LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 가능 여부 확인")
    class IsAvailable {
        @Test
        @DisplayName("사용 가능한 쿠폰은 true를 반환한다")
        void isAvailable_WhenAvailable() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            assertThat(userCoupon.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("만료된 쿠폰은 예외가 발생한다")
        void isAvailable_WhenExpired() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.EXPIRED)
                    .startAt(LocalDateTime.now().minusDays(2))
                    .endAt(LocalDateTime.now().minusDays(1))
                    .build();

            // when & then
            assertThatThrownBy(userCoupon::isAvailable)
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("이미 사용된 쿠폰은 예외가 발생한다")
        void isAvailable_WhenUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.USED)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            assertThatThrownBy(userCoupon::isAvailable)
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("사용 시작일이 지나지 않은 쿠폰은 예외가 발생한다")
        void isAvailable_WhenBeforeStartDate() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().plusDays(1))
                    .endAt(LocalDateTime.now().plusDays(2))
                    .build();

            // when & then
            assertThatThrownBy(userCoupon::isAvailable)
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("쿠폰 만료 여부 확인")
    class IsExpired {
        @Test
        @DisplayName("만료된 쿠폰은 true를 반환한다")
        void isExpired_WhenExpired() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .endAt(LocalDateTime.now().minusDays(1))
                    .build();

            // when & then
            assertThat(userCoupon.isExpired()).isTrue();
        }

        @Test
        @DisplayName("만료되지 않은 쿠폰은 false를 반환한다")
        void isExpired_WhenNotExpired() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            assertThat(userCoupon.isExpired()).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 소유자 확인")
    class IsOwnedBy {
        @Test
        @DisplayName("소유자인 경우 true를 반환한다")
        void isOwnedBy_WhenOwner() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(TEST_USER_ID)
                    .build();

            // when & then
            assertThat(userCoupon.isOwnedBy(TEST_USER_ID)).isTrue();
        }

        @Test
        @DisplayName("소유자가 아닌 경우 false를 반환한다")
        void isOwnedBy_WhenNotOwner() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(TEST_USER_ID)
                    .build();

            // when & then
            assertThat(userCoupon.isOwnedBy(999L)).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 정책 확인")
    class IsIssuedFrom {
        @Test
        @DisplayName("해당 정책에서 발급된 쿠폰은 true를 반환한다")
        void isIssuedFrom_WhenFromPolicy() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();

            // when
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();

            // then
            assertThat(userCoupon.isIssuedFrom(policy)).isTrue();
        }

        @Test
        @DisplayName("다른 정책에서 발급된 쿠폰은 false를 반환한다")
        void isIssuedFrom_WhenFromDifferentPolicy() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();

            // when
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID + 1)
                    .build();

            // then
            assertThat(userCoupon.isIssuedFrom(policy)).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 처리")
    class Use {
        @Test
        @DisplayName("사용 가능한 쿠폰을 사용한다")
        void use_WhenAvailable() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            userCoupon.use();

            // then
            assertThat(userCoupon.getUsageStatus()).isEqualTo(UserCouponStatus.USED);
        }
    }

    @Nested
    @DisplayName("쿠폰 만료 처리")
    class Expire {
        @Test
        @DisplayName("쿠폰을 만료 처리한다")
        void expire() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .build();

            // when
            userCoupon.expire();

            // then
            assertThat(userCoupon.getUsageStatus()).isEqualTo(UserCouponStatus.EXPIRED);
        }
    }

    @Nested
    @DisplayName("쿠폰 복원 처리")
    class Restore {
        @Test
        @DisplayName("사용된 쿠폰을 복원한다")
        void restore_WhenUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.USED)
                    .build();

            // when
            userCoupon.restore();

            // then
            assertThat(userCoupon.getUsageStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
        }

        @Test
        @DisplayName("사용되지 않은 쿠폰은 복원되지 않는다")
        void restore_WhenNotUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .build();

            // when
            userCoupon.restore();

            // then
            assertThat(userCoupon.getUsageStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
        }
    }

    @Nested
    @DisplayName("할인 금액 계산")
    class CalculateDiscountAmount {
        @Test
        @DisplayName("사용 가능한 쿠폰으로 할인 금액을 계산한다")
        void calculateDiscountAmount_WhenAvailable() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.RATE)
                    .discountValue(TEST_DISCOUNT_RATE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();
            int totalAmount = (TEST_MAX_DISCOUNT_AMOUNT - 1) * TEST_DISCOUNT_RATE;
            int expectedDiscountAmount = totalAmount * TEST_DISCOUNT_RATE / 100;

            // when
            int discountAmount = userCoupon.calculateDiscountAmount(totalAmount);

            // then
            assertThat(discountAmount).isEqualTo(expectedDiscountAmount);
        }

        @Test
        @DisplayName("최대 할인 금액을 초과하는 경우 최대 할인 금액을 반환한다")
        void calculateDiscountAmount_WhenExceedsMaxDiscount() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.RATE)
                    .discountValue(TEST_DISCOUNT_RATE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();
            int totalAmount =  (TEST_MAX_DISCOUNT_AMOUNT + 1) * TEST_DISCOUNT_RATE;

            // when
            int discountAmount = userCoupon.calculateDiscountAmount(totalAmount);

            // then
            assertThat(discountAmount).isEqualTo(TEST_MAX_DISCOUNT_AMOUNT);
        }

        @Test
        @DisplayName("최소 주문 금액보다 적은 경우 할인 금액은 0이다")
        void calculateDiscountAmount_WhenBelowMinOrderAmount() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.AMOUNT)
                    .discountValue(TEST_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            long discountAmount = userCoupon.calculateDiscountAmount(TEST_MIN_ORDER_AMOUNT - 1);

            // then
            assertThat(discountAmount).isEqualTo(0);
        }

        @Test
        @DisplayName("사용 불가능한 쿠폰은 예외가 발생한다")
        void calculateDiscountAmount_WhenNotAvailable() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .usageStatus(UserCouponStatus.USED)
                    .discountType(DiscountType.AMOUNT)
                    .discountValue(TEST_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // when-then
            assertThatThrownBy(() -> userCoupon.calculateDiscountAmount(10000))
                    .isInstanceOf(BusinessException.class);
        }
    }
} 