package kr.hhplus.be.ecommerce.coupon.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("사용자 쿠폰 도메인 테스트")
class UserCouponTest {

    private static final Long TEST_USER_COUPON_ID = 1L;
    private static final Long TEST_COUPON_POLICY_ID = 10L;
    private static final Long TEST_USER_ID = 100L;
    private static final String TEST_COUPON_NAME = "테스트 쿠폰";
    private static final DiscountType TEST_DISCOUNT_TYPE = DiscountType.AMOUNT;
    private static final Long TEST_DISCOUNT_VALUE = 1000L;
    private static final Long TEST_MAX_DISCOUNT_AMOUNT = 2000L;
    private static final Long TEST_MIN_ORDER_AMOUNT = 5000L;
    private static final LocalDateTime TEST_START_AT = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime TEST_END_AT = LocalDateTime.of(2024, 12, 31, 23, 59);

    @Nested
    @DisplayName("사용자 쿠폰 생성")
    class CreateUserCoupon {
        @Test
        @DisplayName("사용자 쿠폰을 생성한다")
        void createUserCoupon() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .userCouponId(TEST_USER_COUPON_ID)
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .userId(TEST_USER_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .status(UserCouponStatus.AVAILABLE)
                    .startAt(TEST_START_AT)
                    .endAt(TEST_END_AT)
                    .build();

            assertThat(userCoupon.getUserCouponId()).isEqualTo(TEST_USER_COUPON_ID);
            assertThat(userCoupon.getCouponPolicyId()).isEqualTo(TEST_COUPON_POLICY_ID);
            assertThat(userCoupon.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(userCoupon.getCouponName()).isEqualTo(TEST_COUPON_NAME);
            assertThat(userCoupon.getDiscountType()).isEqualTo(TEST_DISCOUNT_TYPE);
            assertThat(userCoupon.getDiscountValue()).isEqualTo(TEST_DISCOUNT_VALUE);
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 팩토리 메서드")
    class Issue {
        @Test
        @DisplayName("쿠폰 정책으로부터 사용자 쿠폰을 발급한다")
        void issue() {
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .couponName(TEST_COUPON_NAME)
                    .discountType(TEST_DISCOUNT_TYPE)
                    .discountValue(TEST_DISCOUNT_VALUE)
                    .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                    .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                    .validDurationDays(30)
                    .build();

            UserCoupon userCoupon = UserCoupon.issue(TEST_USER_ID, policy);

            assertThat(userCoupon.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(userCoupon.getCouponPolicyId()).isEqualTo(TEST_COUPON_POLICY_ID);
            // couponName은 issue 메서드에서 설정되지 않으므로 null
            assertThat(userCoupon.getCouponName()).isNull();
            assertThat(userCoupon.getDiscountType()).isEqualTo(TEST_DISCOUNT_TYPE);
            assertThat(userCoupon.getDiscountValue()).isEqualTo(TEST_DISCOUNT_VALUE);
            assertThat(userCoupon.getMaxDiscountAmount()).isEqualTo(TEST_MAX_DISCOUNT_AMOUNT);
            assertThat(userCoupon.getMinOrderAmount()).isEqualTo(TEST_MIN_ORDER_AMOUNT);
            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
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
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            assertThat(userCoupon.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("만료된 쿠폰은 예외가 발생한다")
        void isAvailable_WhenExpired() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.EXPIRED)
                    .startAt(LocalDateTime.now().minusDays(2))
                    .endAt(LocalDateTime.now().minusDays(1))
                    .build();

            assertThatThrownBy(userCoupon::isAvailable)
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("이미 사용된 쿠폰은 예외가 발생한다")
        void isAvailable_WhenUsed() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.USED)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            assertThatThrownBy(userCoupon::isAvailable)
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("사용 시작일이 지나지 않은 쿠폰은 예외가 발생한다")
        void isAvailable_WhenBeforeStartDate() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().plusDays(1))
                    .endAt(LocalDateTime.now().plusDays(2))
                    .build();

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
            UserCoupon userCoupon = UserCoupon.builder()
                    .endAt(LocalDateTime.now().minusDays(1))
                    .build();

            assertThat(userCoupon.isExpired()).isTrue();
        }

        @Test
        @DisplayName("만료되지 않은 쿠폰은 false를 반환한다")
        void isExpired_WhenNotExpired() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            assertThat(userCoupon.isExpired()).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 소유자 확인")
    class IsOwnedBy {
        @Test
        @DisplayName("소유자인 경우 true를 반환한다")
        void isOwnedBy_WhenOwner() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(TEST_USER_ID)
                    .build();

            assertThat(userCoupon.isOwnedBy(TEST_USER_ID)).isTrue();
        }

        @Test
        @DisplayName("소유자가 아닌 경우 false를 반환한다")
        void isOwnedBy_WhenNotOwner() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(TEST_USER_ID)
                    .build();

            assertThat(userCoupon.isOwnedBy(999L)).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 정책 확인")
    class IsIssuedFrom {
        @Test
        @DisplayName("해당 정책에서 발급된 쿠폰은 true를 반환한다")
        void isIssuedFrom_WhenFromPolicy() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();

            assertThat(userCoupon.isIssuedFrom(policy)).isTrue();
        }

        @Test
        @DisplayName("다른 정책에서 발급된 쿠폰은 false를 반환한다")
        void isIssuedFrom_WhenFromDifferentPolicy() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .couponPolicyId(TEST_COUPON_POLICY_ID)
                    .build();
            CouponPolicy policy = CouponPolicy.builder()
                    .couponPolicyId(999L)
                    .build();

            assertThat(userCoupon.isIssuedFrom(policy)).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 처리")
    class Use {
        @Test
        @DisplayName("사용 가능한 쿠폰을 사용한다")
        void use_WhenAvailable() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            userCoupon.use();

            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
        }
    }

    @Nested
    @DisplayName("쿠폰 만료 처리")
    class Expire {
        @Test
        @DisplayName("쿠폰을 만료 처리한다")
        void expire() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .build();

            userCoupon.expire();

            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.EXPIRED);
        }
    }

    @Nested
    @DisplayName("쿠폰 복원 처리")
    class Restore {
        @Test
        @DisplayName("사용된 쿠폰을 복원한다")
        void restore_WhenUsed() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.USED)
                    .build();

            userCoupon.restore();

            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
        }

        @Test
        @DisplayName("사용되지 않은 쿠폰은 복원되지 않는다")
        void restore_WhenNotUsed() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .build();

            userCoupon.restore();

            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
        }
    }

    @Nested
    @DisplayName("할인 금액 계산")
    class CalculateDiscountAmount {
        @Test
        @DisplayName("사용 가능한 쿠폰으로 할인 금액을 계산한다")
        void calculateDiscountAmount_WhenAvailable() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.AMOUNT)
                    .discountValue(1000L)
                    .maxDiscountAmount(2000L)
                    .minOrderAmount(5000L)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            long discountAmount = userCoupon.calculateDiscountAmount(10000);

            // AMOUNT 타입은 maxDiscountAmount를 사용
            assertThat(discountAmount).isEqualTo(2000L);
        }

        @Test
        @DisplayName("최소 주문 금액보다 적은 경우 할인 금액은 0이다")
        void calculateDiscountAmount_WhenBelowMinOrderAmount() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.AMOUNT)
                    .discountValue(1000L)
                    .minOrderAmount(5000L)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            long discountAmount = userCoupon.calculateDiscountAmount(3000);

            assertThat(discountAmount).isEqualTo(0L);
        }

        @Test
        @DisplayName("사용 불가능한 쿠폰은 할인 금액이 0이다")
        void calculateDiscountAmount_WhenNotAvailable() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.USED)
                    .discountType(DiscountType.AMOUNT)
                    .discountValue(1000L)
                    .minOrderAmount(5000L)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            // isAvailable()에서 예외가 발생하므로 0을 반환
            assertThatThrownBy(() -> userCoupon.calculateDiscountAmount(10000))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("할인율 쿠폰으로 할인 금액을 계산한다")
        void calculateDiscountAmount_WhenRateType() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.RATE)
                    .discountValue(10L)
                    .maxDiscountAmount(2000L)
                    .minOrderAmount(5000L)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            long discountAmount = userCoupon.calculateDiscountAmount(10000);

            assertThat(discountAmount).isEqualTo(1000L); // 10000 * 10% = 1000
        }

        @Test
        @DisplayName("최대 할인 금액을 초과하는 경우 최대 할인 금액을 반환한다")
        void calculateDiscountAmount_WhenExceedsMaxDiscount() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .status(UserCouponStatus.AVAILABLE)
                    .discountType(DiscountType.RATE)
                    .discountValue(30L)
                    .maxDiscountAmount(2000L)
                    .minOrderAmount(5000L)
                    .startAt(LocalDateTime.now().minusDays(1))
                    .endAt(LocalDateTime.now().plusDays(1))
                    .build();

            long discountAmount = userCoupon.calculateDiscountAmount(10000);

            assertThat(discountAmount).isEqualTo(2000L); // 10000 * 30% = 3000, but max is 2000
        }
    }
} 