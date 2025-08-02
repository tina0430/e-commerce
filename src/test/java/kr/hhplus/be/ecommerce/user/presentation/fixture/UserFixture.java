package kr.hhplus.be.ecommerce.user.presentation.fixture;

import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.TransactionType;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserFixture {

    public static final Integer BASIC_CURSOR_SIZE = 10;
    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_TRANSACTION_ID = 1L;
    public static final Integer TEST_TRANSACTION_AMOUNT = 3000;
    public static final Integer TEST_TRANSACTION_BALANCE = 1000000000;
    public static final Integer TEST_TRANSACTION_SIZE = 100;

    public static UserEntity createUserWithBalance(int balance) {
        return UserEntity.builder()
                .userName("테스트 유저 (" + balance + ")")
                .currentBalance(balance)
                .build();
    }

    public static PointTransactionEntity createChargeTransactionEntity(LocalDateTime createAt) {
        return PointTransactionEntity.builder()
                .userId(TEST_USER_ID)
                .transactionType(TransactionType.CHARGE)
                .amount(TEST_TRANSACTION_AMOUNT)
                .balance(TEST_TRANSACTION_BALANCE + TEST_TRANSACTION_AMOUNT)
                .createdAt(createAt) // 시간 분산
                .build();
    }

    public static List<PointTransactionEntity> createTransactionEntityList() {
        List<PointTransactionEntity> transactions = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusSeconds(TEST_TRANSACTION_SIZE);
        for (long i = 0; i < TEST_TRANSACTION_SIZE; i++) {
            transactions.add(createChargeTransactionEntity(baseTime.plusSeconds(i)));
        }
        return transactions;
    }

}
