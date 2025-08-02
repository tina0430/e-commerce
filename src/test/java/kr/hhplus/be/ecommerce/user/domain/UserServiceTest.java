package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.TransactionType;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 도메인 서비스 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPersistenceMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private User user;
    private PointTransactionEntity pointTransactionEntity;
    private PointTransaction pointTransaction;

    // 테스트 상수 정의
    private static final Long TEST_USER_ID = 1L;
    private static final Integer TEST_BALANCE = 10000;
    private static final Integer TEST_CHARGE_AMOUNT = 5000;
    private static final Integer TEST_USE_AMOUNT = 3000;
    private static final Long TEST_TRANSACTION_ID = 1L;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        userEntity = UserEntity.builder()
                .userId(TEST_USER_ID)
                .userName("테스트 유저")
                .currentBalance(TEST_BALANCE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .userId(TEST_USER_ID)
                .userName("테스트 유저")
                .currentBalance(TEST_BALANCE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pointTransactionEntity = PointTransactionEntity.builder()
                .transactionId(TEST_TRANSACTION_ID)
                .userId(TEST_USER_ID)
                .transactionType(TransactionType.CHARGE)
                .amount(TEST_CHARGE_AMOUNT)
                .balance(TEST_BALANCE + TEST_CHARGE_AMOUNT)
                .createdAt(LocalDateTime.now())
                .build();

        pointTransaction = PointTransaction.builder()
                .transactionId(TEST_TRANSACTION_ID)
                .userId(TEST_USER_ID)
                .transactionType(TransactionType.CHARGE)
                .amount(TEST_CHARGE_AMOUNT)
                .balance(TEST_BALANCE + TEST_CHARGE_AMOUNT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("포인트 잔액 조회 테스트")
    class BalanceQueryTests {

        @Test
        @DisplayName("사용자의 포인트 잔액을 조회한다")
        void getCurrentBalance() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);

            // when
            Integer result = userService.getCurrentBalance(TEST_USER_ID);

            // then
            assertThat(result).isEqualTo(TEST_BALANCE);
            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 잔액을 조회하면 예외가 발생한다")
        void getCurrentBalance_UserNotFound() {
            // given
            when(userRepository.findUserById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getCurrentBalance(999L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(999L);
            verify(userMapper, never()).toUser(any());
        }
    }

    @Nested
    @DisplayName("포인트 충전 테스트")
    class PointChargeTests {

        @Test
        @DisplayName("포인트를 정상적으로 충전한다")
        void chargePoint() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);

            // when
            User result = userService.chargePoint(TEST_USER_ID, TEST_CHARGE_AMOUNT);

            // then
            assertThat(result.getCurrentBalance()).isEqualTo(TEST_BALANCE + TEST_CHARGE_AMOUNT);
            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
        }

        @Test
        @DisplayName("존재하지 않는 사용자에게 포인트를 충전하려 하면 예외가 발생한다")
        void chargePoint_UserNotFound() {
            // given
            when(userRepository.findUserById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.chargePoint(999L, TEST_CHARGE_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(999L);
            verify(userMapper, never()).toUser(any());
            // save 메서드 검증은 내부 구현이므로 제외
        }

        @Test
        @DisplayName("0 포인트를 충전한다")
        void chargePoint_ZeroAmount() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);

            // when
            User result = userService.chargePoint(TEST_USER_ID, 0);

            // then
            assertThat(result.getCurrentBalance()).isEqualTo(TEST_BALANCE);
            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
        }
    }

    @Nested
    @DisplayName("포인트 사용 테스트")
    class PointUsageTests {

        @Test
        @DisplayName("포인트를 정상적으로 사용한다")
        void usePoint() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);
            // applyToEntity와 save는 내부 구현이므로 테스트에서 제외

            // when
            User result = userService.usePoint(TEST_USER_ID, TEST_USE_AMOUNT);

            // then
            assertThat(result.getCurrentBalance()).isEqualTo(TEST_BALANCE - TEST_USE_AMOUNT);
            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
            // applyToEntity와 save 검증은 내부 구현이므로 제외
        }

        @Test
        @DisplayName("존재하지 않는 사용자가 포인트를 사용하려 하면 예외가 발생한다")
        void usePoint_UserNotFound() {
            // given
            when(userRepository.findUserById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.usePoint(999L, TEST_USE_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(999L);
            verify(userMapper, never()).toUser(any());
            // save 메서드 검증은 내부 구현이므로 제외
        }

        @Test
        @DisplayName("부족한 포인트로 사용하려 하면 예외가 발생한다")
        void usePoint_InsufficientBalance() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);

            // when & then
            assertThatThrownBy(() -> userService.usePoint(TEST_USER_ID, TEST_BALANCE + 1000))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.INSUFFICIENT_POINT.getCode());
                    });

            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
        }

        @Test
        @DisplayName("0 포인트를 사용한다")
        void usePoint_ZeroAmount() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userMapper.toUser(userEntity)).thenReturn(user);
            // applyToEntity와 save는 내부 구현이므로 테스트에서 제외

            // when
            User result = userService.usePoint(TEST_USER_ID, 0);

            // then
            assertThat(result.getCurrentBalance()).isEqualTo(TEST_BALANCE);
            verify(userRepository, atLeastOnce()).findUserById(TEST_USER_ID);
            verify(userMapper).toUser(userEntity);
            // applyToEntity 검증은 내부 구현이므로 제외
            // save 검증은 내부 구현이므로 제외
        }
    }

    @Nested
    @DisplayName("포인트 거래 내역 조회 테스트")
    class PointHistoryTests {

        @Test
        @DisplayName("사용자의 포인트 거래 내역을 조회한다")
        void getPointHistory() {
            // given
            List<PointTransactionEntity> transactionEntities = List.of(pointTransactionEntity);
            List<PointTransaction> expectedTransactions = List.of(pointTransaction);

            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userRepository.findTransactionsByUserIdOrderByCreatedAtDesc(TEST_USER_ID)).thenReturn(transactionEntities);
            when(userMapper.toPointTransactionList(transactionEntities)).thenReturn(expectedTransactions);

            // when
            List<PointTransaction> result = userService.getPointHistory(TEST_USER_ID);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTransactionId()).isEqualTo(TEST_TRANSACTION_ID);
            assertThat(result.get(0).getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(result.get(0).getTransactionType()).isEqualTo(TransactionType.CHARGE);

            verify(userRepository).findUserById(TEST_USER_ID);
            verify(userRepository).findTransactionsByUserIdOrderByCreatedAtDesc(TEST_USER_ID);
            verify(userMapper).toPointTransactionList(transactionEntities);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 거래 내역을 조회하려 하면 예외가 발생한다")
        void getPointHistory_UserNotFound() {
            // given
            when(userRepository.findUserById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getPointHistory(999L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(999L);
            verify(userRepository, never()).findTransactionsByUserIdOrderByCreatedAtDesc(anyLong());
            verify(userMapper, never()).toPointTransactionList(any());
        }

        @Test
        @DisplayName("거래 내역이 없는 사용자의 내역을 조회한다")
        void getPointHistory_EmptyHistory() {
            // given
            when(userRepository.findUserById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
            when(userRepository.findTransactionsByUserIdOrderByCreatedAtDesc(TEST_USER_ID)).thenReturn(List.of());
            when(userMapper.toPointTransactionList(List.of())).thenReturn(List.of());

            // when
            List<PointTransaction> result = userService.getPointHistory(TEST_USER_ID);

            // then
            assertThat(result).isEmpty();

            verify(userRepository).findUserById(TEST_USER_ID);
            verify(userRepository).findTransactionsByUserIdOrderByCreatedAtDesc(TEST_USER_ID);
            verify(userMapper).toPointTransactionList(List.of());
        }
    }

    @Nested
    @DisplayName("에러 처리 테스트")
    class ErrorHandlingTests {

        @Test
        @DisplayName("null 사용자 ID로 조회하면 예외가 발생한다")
        void getCurrentBalance_NullUserId() {
            // when & then
            assertThatThrownBy(() -> userService.getCurrentBalance(null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(null);
        }

        @Test
        @DisplayName("음수 사용자 ID로 조회하면 예외가 발생한다")
        void getCurrentBalance_NegativeUserId() {
            // when & then
            assertThatThrownBy(() -> userService.getCurrentBalance(-1L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_NOT_FOUND.getCode());
                    });

            verify(userRepository).findUserById(-1L);
        }
    }
} 