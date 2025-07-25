package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.TransactionType;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userMapper;

    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     */
    public Long getBalance(Long userId) {
        UserEntity userEntity = findUser(userId);
        return userMapper.toUser(userEntity).getBalance();
    }

    /**
     * 포인트를 충전합니다.
     * @param userId 사용자 ID
     * @param amount 충전할 포인트 금액
     * @return 충전 후 포인트 정보
     */
    @Transactional
    public User chargePoint(Long userId, long amount) { // todo 어플리케이션 레이어인가..
        UserEntity userEntity = findUser(userId);
        User user = userMapper.toUser(userEntity);
        user.chargePoint(amount);
        userMapper.applyToEntity(user, userEntity);
        // todo 이걸 반환해야 하나?
        createPointTransaction(userId, TransactionType.CHARGE, amount, user.getBalance());
        return user;
    }

    /**
     * 포인트를 사용합니다.
     * @param userId 사용자 ID
     * @param amount 사용할 포인트 금액
     * @return 사용 후 포인트 정보
     */
    @Transactional
    public User usePoint(Long userId, long amount) {
        UserEntity userEntity = findUser(userId);
        User user = userMapper.toUser(userEntity);
        user.usePoint(amount);
        userMapper.applyToEntity(user, userEntity);
        // todo 이걸 반환해야 하나?
        createPointTransaction(userId, TransactionType.USE, amount, user.getBalance());
        return user;
    }

    /**
     * 포인트 거래 내역을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 거래 내역 목록
     */
    public List<PointTransaction> getPointHistory(Long userId) {
        UserEntity userEntity = findUser(userId);
        List<PointTransactionEntity> transactionEntities = userRepository.findTransactionsByUserIdOrderByCreatedAtDesc(userEntity.getUserId());
        return userMapper.toPointTransactionList(transactionEntities);
    }

    /**
     * 포인트 거래 내역을 생성합니다.
     * @param userId 사용자 ID
     * @param transactionType 거래 타입
     * @param amount 거래 금액
     * @param balance 거래 후 잔액
     */
    private void createPointTransaction(Long userId, TransactionType transactionType, long amount, long balance) {
        UserEntity userEntity = findUser(userId);
        PointTransactionEntity transactionEntity = PointTransactionEntity.builder()
                .userId(userEntity.getUserId())
                .transactionType(transactionType)
                .amount(amount)
                .balance(balance)
                .build();

        userRepository.save(transactionEntity);
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_FOUND));
    }
}
