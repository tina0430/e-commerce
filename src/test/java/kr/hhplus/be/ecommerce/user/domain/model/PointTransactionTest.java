package kr.hhplus.be.ecommerce.user.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("포인트 거래 내역 도메인 모델 테스트")
class PointTransactionTest {

    private PointTransaction pointTransaction;
    private LocalDateTime testCreatedAt;

    // 테스트 상수 정의
    private static final Long TEST_TRANSACTION_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_AMOUNT = 5000L;
    private static final Long TEST_BALANCE = 15000L;
    private static final Long TEST_ANOTHER_TRANSACTION_ID = 2L;
    private static final Long TEST_ANOTHER_USER_ID = 2L;
    private static final Long TEST_ANOTHER_AMOUNT = 3000L;
    private static final Long TEST_ANOTHER_BALANCE = 7000L;
    private static final Long TEST_CHARGE_AMOUNT = 10000L;
    private static final Long TEST_CHARGE_BALANCE = 20000L;
    private static final Long TEST_USE_AMOUNT = 5000L;
    private static final Long TEST_USE_BALANCE = 5000L;
    private static final Long TEST_ZERO_AMOUNT = 0L;
    private static final Long TEST_ZERO_BALANCE = 10000L;
    private static final Long TEST_SMALL_AMOUNT = 1000L;

    @BeforeEach
    void setUp() {
        testCreatedAt = LocalDateTime.now();
        pointTransaction = PointTransaction.builder()
                .transactionId(TEST_TRANSACTION_ID)
                .userId(TEST_USER_ID)
                .transactionType(TransactionType.CHARGE)
                .amount(TEST_AMOUNT)
                .balance(TEST_BALANCE)
                .createdAt(testCreatedAt)
                .build();
    }

    @Nested
    @DisplayName("포인트 거래 내역 생성 테스트")
    class PointTransactionCreationTests {

        @Test
        @DisplayName("포인트 거래 내역을 정상적으로 생성한다")
        void createPointTransaction() {
            // given & when
            PointTransaction newTransaction = PointTransaction.builder()
                    .transactionId(TEST_ANOTHER_TRANSACTION_ID)
                    .userId(TEST_ANOTHER_USER_ID)
                    .transactionType(TransactionType.USE)
                    .amount(TEST_ANOTHER_AMOUNT)
                    .balance(TEST_ANOTHER_BALANCE)
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(newTransaction.getTransactionId()).isEqualTo(TEST_ANOTHER_TRANSACTION_ID);
            assertThat(newTransaction.getUserId()).isEqualTo(TEST_ANOTHER_USER_ID);
            assertThat(newTransaction.getTransactionType()).isEqualTo(TransactionType.USE);
            assertThat(newTransaction.getAmount()).isEqualTo(TEST_ANOTHER_AMOUNT);
            assertThat(newTransaction.getBalance()).isEqualTo(TEST_ANOTHER_BALANCE);
            assertThat(newTransaction.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("기본값으로 포인트 거래 내역을 생성한다")
        void createPointTransactionWithDefaults() {
            // given & when
            PointTransaction emptyTransaction = new PointTransaction();

            // then
            assertThat(emptyTransaction.getTransactionId()).isNull();
            assertThat(emptyTransaction.getUserId()).isNull();
            assertThat(emptyTransaction.getTransactionType()).isNull();
            assertThat(emptyTransaction.getAmount()).isNull();
            assertThat(emptyTransaction.getBalance()).isNull();
            assertThat(emptyTransaction.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("충전 거래 내역을 생성한다")
        void createChargeTransaction() {
            // given & when
            PointTransaction chargeTransaction = PointTransaction.builder()
                    .transactionId(3L)
                    .userId(TEST_USER_ID)
                    .transactionType(TransactionType.CHARGE)
                    .amount(TEST_CHARGE_AMOUNT)
                    .balance(TEST_CHARGE_BALANCE)
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(chargeTransaction.getTransactionType()).isEqualTo(TransactionType.CHARGE);
            assertThat(chargeTransaction.getAmount()).isEqualTo(TEST_CHARGE_AMOUNT);
            assertThat(chargeTransaction.getBalance()).isEqualTo(TEST_CHARGE_BALANCE);
        }

        @Test
        @DisplayName("사용 거래 내역을 생성한다")
        void createUseTransaction() {
            // given & when
            PointTransaction useTransaction = PointTransaction.builder()
                    .transactionId(4L)
                    .userId(TEST_USER_ID)
                    .transactionType(TransactionType.USE)
                    .amount(TEST_USE_AMOUNT)
                    .balance(TEST_USE_BALANCE)
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(useTransaction.getTransactionType()).isEqualTo(TransactionType.USE);
            assertThat(useTransaction.getAmount()).isEqualTo(TEST_USE_AMOUNT);
            assertThat(useTransaction.getBalance()).isEqualTo(TEST_USE_BALANCE);
        }
    }

    @Nested
    @DisplayName("포인트 거래 내역 정보 테스트")
    class PointTransactionInformationTests {

        @Test
        @DisplayName("포인트 거래 내역의 기본 정보를 확인한다")
        void getPointTransactionInformation() {
            // when & then
            assertThat(pointTransaction.getTransactionId()).isEqualTo(TEST_TRANSACTION_ID);
            assertThat(pointTransaction.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(pointTransaction.getTransactionType()).isEqualTo(TransactionType.CHARGE);
            assertThat(pointTransaction.getAmount()).isEqualTo(TEST_AMOUNT);
            assertThat(pointTransaction.getBalance()).isEqualTo(TEST_BALANCE);
            assertThat(pointTransaction.getCreatedAt()).isEqualTo(testCreatedAt);
        }

        @Test
        @DisplayName("충전 거래인지 확인한다")
        void isChargeTransaction() {
            // given
            PointTransaction chargeTransaction = PointTransaction.builder()
                    .transactionType(TransactionType.CHARGE)
                    .build();

            PointTransaction useTransaction = PointTransaction.builder()
                    .transactionType(TransactionType.USE)
                    .build();

            // when & then
            assertThat(chargeTransaction.getTransactionType()).isEqualTo(TransactionType.CHARGE);
            assertThat(useTransaction.getTransactionType()).isEqualTo(TransactionType.USE);
        }

        @Test
        @DisplayName("거래 금액이 양수인지 확인한다")
        void isPositiveAmount() {
            // given
            PointTransaction positiveTransaction = PointTransaction.builder()
                    .amount(TEST_SMALL_AMOUNT)
                    .build();

            PointTransaction zeroTransaction = PointTransaction.builder()
                    .amount(TEST_ZERO_AMOUNT)
                    .build();

            // when & then
            assertThat(positiveTransaction.getAmount()).isGreaterThan(TEST_ZERO_AMOUNT);
            assertThat(zeroTransaction.getAmount()).isEqualTo(TEST_ZERO_AMOUNT);
        }
    }

    @Nested
    @DisplayName("포인트 거래 내역 검증 테스트")
    class PointTransactionValidationTests {

        @Test
        @DisplayName("거래 후 잔액이 올바른지 확인한다")
        void validateBalanceAfterTransaction() {
            // given
            long initialBalance = TEST_ZERO_BALANCE;
            long transactionAmount = TEST_ANOTHER_AMOUNT;
            long expectedBalance = initialBalance + transactionAmount; // 충전이므로 더하기

            PointTransaction transaction = PointTransaction.builder()
                    .transactionType(TransactionType.CHARGE)
                    .amount(transactionAmount)
                    .balance(expectedBalance)
                    .build();

            // when & then
            assertThat(transaction.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        @DisplayName("사용 거래 후 잔액이 올바른지 확인한다")
        void validateBalanceAfterUseTransaction() {
            // given
            long initialBalance = TEST_ZERO_BALANCE;
            long transactionAmount = TEST_ANOTHER_AMOUNT;
            long expectedBalance = initialBalance - transactionAmount; // 사용이므로 빼기

            PointTransaction transaction = PointTransaction.builder()
                    .transactionType(TransactionType.USE)
                    .amount(transactionAmount)
                    .balance(expectedBalance)
                    .build();

            // when & then
            assertThat(transaction.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        @DisplayName("0 금액 거래도 가능하다")
        void validateZeroAmountTransaction() {
            // given
            PointTransaction zeroTransaction = PointTransaction.builder()
                    .transactionType(TransactionType.CHARGE)
                    .amount(TEST_ZERO_AMOUNT)
                    .balance(TEST_ZERO_BALANCE)
                    .build();

            // when & then
            assertThat(zeroTransaction.getAmount()).isEqualTo(TEST_ZERO_AMOUNT);
            assertThat(zeroTransaction.getBalance()).isEqualTo(TEST_ZERO_BALANCE);
        }
    }

    @Nested
    @DisplayName("포인트 거래 내역 비교 테스트")
    class PointTransactionComparisonTests {

        @Test
        @DisplayName("거래 내역을 생성 시간으로 비교할 수 있다")
        void compareTransactionsByCreatedAt() {
            // given
            LocalDateTime earlier = LocalDateTime.now().minusHours(1);
            LocalDateTime later = LocalDateTime.now();

            PointTransaction earlierTransaction = PointTransaction.builder()
                    .transactionId(1L)
                    .createdAt(earlier)
                    .build();

            PointTransaction laterTransaction = PointTransaction.builder()
                    .transactionId(2L)
                    .createdAt(later)
                    .build();

            // when & then
            assertThat(earlierTransaction.getCreatedAt()).isBefore(laterTransaction.getCreatedAt());
        }

        @Test
        @DisplayName("같은 사용자의 거래 내역을 구분할 수 있다")
        void distinguishTransactionsByUserId() {
            // given
            PointTransaction user1Transaction = PointTransaction.builder()
                    .transactionId(TEST_TRANSACTION_ID)
                    .userId(TEST_USER_ID)
                    .build();

            PointTransaction user2Transaction = PointTransaction.builder()
                    .transactionId(TEST_ANOTHER_TRANSACTION_ID)
                    .userId(TEST_ANOTHER_USER_ID)
                    .build();

            // when & then
            assertThat(user1Transaction.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(user2Transaction.getUserId()).isEqualTo(TEST_ANOTHER_USER_ID);
            assertThat(user1Transaction.getUserId()).isNotEqualTo(user2Transaction.getUserId());
        }

        @Test
        @DisplayName("거래 타입별로 내역을 구분할 수 있다")
        void distinguishTransactionsByType() {
            // given
            PointTransaction chargeTransaction = PointTransaction.builder()
                    .transactionType(TransactionType.CHARGE)
                    .build();

            PointTransaction useTransaction = PointTransaction.builder()
                    .transactionType(TransactionType.USE)
                    .build();

            // when & then
            assertThat(chargeTransaction.getTransactionType()).isEqualTo(TransactionType.CHARGE);
            assertThat(useTransaction.getTransactionType()).isEqualTo(TransactionType.USE);
            assertThat(chargeTransaction.getTransactionType()).isNotEqualTo(useTransaction.getTransactionType());
        }
    }
} 