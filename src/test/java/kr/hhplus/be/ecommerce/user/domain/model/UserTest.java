package kr.hhplus.be.ecommerce.user.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("유저 도메인 모델 테스트")
class UserTest {

    private User user;

    // 테스트 상수 정의
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_NAME = "테스트 유저";
    private static final Long TEST_BALANCE = 10000L;
    private static final Long TEST_CHARGE_AMOUNT = 5000L;
    private static final Long TEST_USE_AMOUNT = 3000L;
    private static final Long TEST_INSUFFICIENT_AMOUNT = 15000L;
    private static final Long TEST_ZERO_AMOUNT = 0L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(TEST_USER_ID)
                .userName(TEST_USER_NAME)
                .balance(TEST_BALANCE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("포인트 충분 여부 확인 테스트")
    class PointSufficiencyTests {

        @Test
        @DisplayName("충분한 포인트가 있을 때 true를 반환한다")
        void hasEnoughPoint_WithSufficientBalance() {
            // when & then
            assertThat(user.hasEnoughPoint(TEST_CHARGE_AMOUNT)).isTrue();
            assertThat(user.hasEnoughPoint(TEST_BALANCE)).isTrue();
        }

        @Test
        @DisplayName("부족한 포인트가 있을 때 false를 반환한다")
        void hasEnoughPoint_WithInsufficientBalance() {
            // when & then
            assertThat(user.hasEnoughPoint(TEST_INSUFFICIENT_AMOUNT)).isFalse();
        }

        @Test
        @DisplayName("0 포인트 사용 시 true를 반환한다")
        void hasEnoughPoint_ZeroAmount() {
            // when & then
            assertThat(user.hasEnoughPoint(TEST_ZERO_AMOUNT)).isTrue();
        }
    }

    @Nested
    @DisplayName("포인트 충전 테스트")
    class PointChargeTests {

        @Test
        @DisplayName("포인트를 정상적으로 충전한다")
        void chargePoint_PositiveAmount() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.chargePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance + TEST_CHARGE_AMOUNT);
        }

        @Test
        @DisplayName("0 포인트 충전 시 잔액이 변하지 않는다")
        void chargePoint_ZeroAmount() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.chargePoint(TEST_ZERO_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance);
        }

        @Test
        @DisplayName("음수 포인트 충전 시 잔액이 감소한다")
        void chargePoint_NegativeAmount() {
            // given
            long originalBalance = user.getBalance();
            long chargeAmount = -3000L;

            // when
            user.chargePoint(chargeAmount);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance + chargeAmount);
        }

        @Test
        @DisplayName("여러 번 포인트를 충전할 수 있다")
        void chargePoint_MultipleCharges() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.chargePoint(1000L);
            user.chargePoint(2000L);
            user.chargePoint(3000L);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance + 6000L);
        }
    }

    @Nested
    @DisplayName("포인트 사용 테스트")
    class PointUsageTests {

        @Test
        @DisplayName("충분한 포인트가 있을 때 정상적으로 사용한다")
        void usePoint_WithSufficientBalance() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.usePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance - TEST_CHARGE_AMOUNT);
        }

        @Test
        @DisplayName("정확히 잔액만큼 포인트를 사용할 수 있다")
        void usePoint_ExactBalance() {
            // given
            long useAmount = user.getBalance();

            // when
            user.usePoint(useAmount);

            // then
            assertThat(user.getBalance()).isEqualTo(TEST_ZERO_AMOUNT);
        }

        @Test
        @DisplayName("부족한 포인트로 사용하려 하면 예외가 발생한다")
        void usePoint_WithInsufficientBalance() {
            // given
            long useAmount = TEST_INSUFFICIENT_AMOUNT; // 잔액(TEST_BALANCE)보다 많은 금액

            // when & then
            assertThatThrownBy(() -> user.usePoint(useAmount))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.INSUFFICIENT_POINT.getCode());
                    });
        }

        @Test
        @DisplayName("0 포인트 사용 시 잔액이 변하지 않는다")
        void usePoint_ZeroAmount() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.usePoint(TEST_ZERO_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance);
        }

        @Test
        @DisplayName("여러 번 포인트를 사용할 수 있다")
        void usePoint_MultipleUses() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.usePoint(2000L);
            user.usePoint(3000L);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance - 5000L);
        }
    }

    @Nested
    @DisplayName("유저 생성 테스트")
    class UserCreationTests {

        @Test
        @DisplayName("유저를 정상적으로 생성한다")
        void createUser() {
            // given & when
            User newUser = User.builder()
                    .userId(2L)
                    .userName("새로운 유저")
                    .balance(TEST_CHARGE_AMOUNT)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(newUser.getUserId()).isEqualTo(2L);
            assertThat(newUser.getUserName()).isEqualTo("새로운 유저");
            assertThat(newUser.getBalance()).isEqualTo(TEST_CHARGE_AMOUNT);
            assertThat(newUser.getCreatedAt()).isNotNull();
            assertThat(newUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("기본값으로 유저를 생성한다")
        void createUserWithDefaults() {
            // given & when
            User emptyUser = new User();

            // then
            assertThat(emptyUser.getUserId()).isNull();
            assertThat(emptyUser.getUserName()).isNull();
            assertThat(emptyUser.getBalance()).isNull();
            assertThat(emptyUser.getCreatedAt()).isNull();
            assertThat(emptyUser.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("0 잔액으로 유저를 생성할 수 있다")
        void createUserWithZeroBalance() {
            // given & when
            User zeroBalanceUser = User.builder()
                    .userId(3L)
                    .userName("0잔액 유저")
                    .balance(TEST_ZERO_AMOUNT)
                    .build();

            // then
            assertThat(zeroBalanceUser.getBalance()).isEqualTo(TEST_ZERO_AMOUNT);
            assertThat(zeroBalanceUser.hasEnoughPoint(TEST_ZERO_AMOUNT)).isTrue();
            assertThat(zeroBalanceUser.hasEnoughPoint(1L)).isFalse();
        }
    }

    @Nested
    @DisplayName("포인트 연산 복합 테스트")
    class PointOperationCombinationTests {

        @Test
        @DisplayName("충전 후 사용이 정상적으로 작동한다")
        void chargeThenUsePoint() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.chargePoint(TEST_CHARGE_AMOUNT);
            user.usePoint(TEST_USE_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance + 2000L);
        }

        @Test
        @DisplayName("사용 후 충전이 정상적으로 작동한다")
        void useThenChargePoint() {
            // given
            long originalBalance = user.getBalance();

            // when
            user.usePoint(TEST_USE_AMOUNT);
            user.chargePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getBalance()).isEqualTo(originalBalance + 2000L);
        }

        @Test
        @DisplayName("잔액이 부족할 때 충전 후 사용이 가능하다")
        void chargeAfterInsufficientUse() {
            // given
            long originalBalance = user.getBalance();

            // when & then
            assertThatThrownBy(() -> user.usePoint(TEST_INSUFFICIENT_AMOUNT))
                    .isInstanceOf(BusinessException.class);

            // 충전 후 사용
            user.chargePoint(TEST_BALANCE);
            user.usePoint(TEST_INSUFFICIENT_AMOUNT);

            assertThat(user.getBalance()).isEqualTo(originalBalance - 5000L);
        }
    }
} 