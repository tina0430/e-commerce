package kr.hhplus.be.ecommerce.user.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("유저 도메인 모델 테스트")
class UserTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_NAME = "테스트 유저";
    private static final Integer TEST_BALANCE = 10000;
    private static final Integer TEST_CHARGE_AMOUNT = 5000;
    private static final Integer TEST_USE_AMOUNT = 3000;
    private static final Integer TEST_INSUFFICIENT_AMOUNT = 15000;
    private static final Integer TEST_ZERO_AMOUNT = 0;

    @Nested
    @DisplayName("포인트 충분 여부 확인")
    class HasEnoughPointTests {

        @Test
        @DisplayName("충분한 포인트가 있을 때 true를 반환한다")
        void hasEnoughPoint_WithSufficientBalance() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when & then
            assertThat(user.hasEnoughPoint(TEST_CHARGE_AMOUNT)).isTrue();
            assertThat(user.hasEnoughPoint(TEST_BALANCE)).isTrue();
        }

        @Test
        @DisplayName("부족한 포인트가 있을 때 false를 반환한다")
        void hasEnoughPoint_WithInsufficientBalance() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when & then
            assertThat(user.hasEnoughPoint(TEST_INSUFFICIENT_AMOUNT)).isFalse();
        }

        @Test
        @DisplayName("0원 요청 시 true를 반환한다")
        void hasEnoughPoint_WithZeroAmount() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when & then
            assertThat(user.hasEnoughPoint(TEST_ZERO_AMOUNT)).isTrue();
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    class ChargePointTests {

        @Test
        @DisplayName("포인트를 정상적으로 충전한다")
        void chargePoint() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.chargePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + TEST_CHARGE_AMOUNT);
        }

        @Test
        @DisplayName("0원을 충전한다")
        void chargePoint_ZeroAmount() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.chargePoint(TEST_ZERO_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance);
        }

        @Test
        @DisplayName("음수가 아닌 양수를 충전한다")
        void chargePoint_PositiveAmount() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            Integer chargeAmount = 2000;
            user.chargePoint(chargeAmount);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + chargeAmount);
        }

        @Test
        @DisplayName("여러 번 충전한다")
        void chargePoint_MultipleCharges() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.chargePoint(1000);
            user.chargePoint(2000);
            user.chargePoint(3000);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + 6000);
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    class UsePointTests {

        @Test
        @DisplayName("포인트를 정상적으로 사용한다")
        void usePoint() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.usePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance - TEST_CHARGE_AMOUNT);
        }

        @Test
        @DisplayName("전체 잔액을 사용한다")
        void usePoint_AllBalance() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer useAmount = user.getCurrentBalance();
            user.usePoint(useAmount);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(TEST_ZERO_AMOUNT);
        }

        @Test
        @DisplayName("부족한 포인트로 사용하려 하면 예외가 발생한다")
        void usePoint_InsufficientBalance() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when & then
            int useAmount = TEST_BALANCE + 1000;
            assertThatThrownBy(() -> user.usePoint(useAmount))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.INSUFFICIENT_POINT.getCode());
                    });
        }

        @Test
        @DisplayName("0원을 사용한다")
        void usePoint_ZeroAmount() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.usePoint(TEST_ZERO_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance);
        }

        @Test
        @DisplayName("여러 번 사용한다")
        void usePoint_MultipleUses() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.usePoint(2000);
            user.usePoint(3000);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance - 5000);
        }
    }

    @Nested
    @DisplayName("User 생성")
    class UserCreationTests {

        @Test
        @DisplayName("모든 필드가 있는 User를 생성한다")
        void createUser_WithAllFields() {
            // given & when
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_CHARGE_AMOUNT)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(user.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(user.getUserName()).isEqualTo(TEST_USER_NAME);
            assertThat(user.getCurrentBalance()).isEqualTo(TEST_CHARGE_AMOUNT);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("최소 필드로 User를 생성한다")
        void createUser_WithMinimalFields() {
            // given & when
            User emptyUser = User.builder().build();

            // then
            assertThat(emptyUser.getUserId()).isNull();
            assertThat(emptyUser.getUserName()).isNull();
            assertThat(emptyUser.getCurrentBalance()).isNull();
            assertThat(emptyUser.getCreatedAt()).isNull();
            assertThat(emptyUser.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("0 잔액으로 User를 생성한다")
        void createUser_WithZeroBalance() {
            // given & when
            User zeroBalanceUser = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_ZERO_AMOUNT)
                    .build();

            // then
            assertThat(zeroBalanceUser.getCurrentBalance()).isEqualTo(TEST_ZERO_AMOUNT);
            assertThat(zeroBalanceUser.hasEnoughPoint(TEST_ZERO_AMOUNT)).isTrue();
            assertThat(zeroBalanceUser.hasEnoughPoint(1)).isFalse();
        }
    }

    @Nested
    @DisplayName("포인트 충전 및 사용 조합")
    class ChargeAndUsePointTests {

        @Test
        @DisplayName("충전 후 사용한다")
        void chargeThenUsePoint() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.chargePoint(TEST_CHARGE_AMOUNT);
            user.usePoint(TEST_USE_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + 2000);
        }

        @Test
        @DisplayName("사용 후 충전한다")
        void useThenChargePoint() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            user.usePoint(TEST_USE_AMOUNT);
            user.chargePoint(TEST_CHARGE_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + 2000);
        }

        @Test
        @DisplayName("부족한 포인트 사용 후 충전한다")
        void useInsufficientThenChargePoint() {
            // given
            User user = User.builder()
                    .userId(TEST_USER_ID)
                    .userName(TEST_USER_NAME)
                    .currentBalance(TEST_BALANCE)
                    .build();

            // when
            Integer originalBalance = user.getCurrentBalance();
            assertThatThrownBy(() -> user.usePoint(TEST_INSUFFICIENT_AMOUNT))
                    .isInstanceOf(BusinessException.class);

            // 충전 후 사용
            user.chargePoint(TEST_BALANCE);
            user.usePoint(TEST_INSUFFICIENT_AMOUNT);

            // then
            assertThat(user.getCurrentBalance()).isEqualTo(originalBalance + TEST_BALANCE - TEST_INSUFFICIENT_AMOUNT);
        }
    }
} 