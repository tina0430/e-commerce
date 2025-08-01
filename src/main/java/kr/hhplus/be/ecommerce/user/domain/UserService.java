package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.common.dto.PageRequest;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
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

import java.time.LocalDateTime;
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
    public Integer getCurrentBalance(Long userId) {
        UserEntity userEntity = findUser(userId);
        return userMapper.toUser(userEntity).getCurrentBalance();
    }

    /**
     * 포인트를 충전합니다.
     * @param userId 사용자 ID
     * @param amount 충전할 포인트 금액
     * @return 충전 후 포인트 정보
     */
    @Transactional
    public User chargePoint(Long userId, int amount) { // todo 어플리케이션 레이어인가..
        UserEntity userEntity = findUser(userId);
        User user = userMapper.toUser(userEntity);
        user.chargePoint(amount);
        userMapper.applyToEntity(user, userEntity);
        // todo 이걸 반환해야 하나?
        createPointTransaction(userId, TransactionType.CHARGE, amount, user.getCurrentBalance());
        return user;
    }

    /**
     * 포인트를 사용합니다.
     * @param userId 사용자 ID
     * @param amount 사용할 포인트 금액
     * @return 사용 후 포인트 정보
     */
    @Transactional
    public User usePoint(Long userId, int amount) {
        UserEntity userEntity = findUser(userId);
        User user = userMapper.toUser(userEntity);
        user.usePoint(amount);
        userMapper.applyToEntity(user, userEntity);
        // todo 이걸 반환해야 하나?
        createPointTransaction(userId, TransactionType.USE, amount, user.getCurrentBalance());
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
     * 포인트 거래 내역을 페이징으로 조회합니다.
     * @param userId 사용자 ID
     * @param pageRequest 페이징 요청 정보
     * @return 페이징된 포인트 거래 내역
     */
    public PageResponse<PointTransaction> getPointHistoryWithPaging(Long userId, PageRequest pageRequest) {
        UserEntity userEntity = findUser(userId);
        
        // 커서가 없으면 현재 시간을 기준으로 설정
        LocalDateTime cursor = pageRequest.getCursor() != null ? 
                pageRequest.getCursor() : LocalDateTime.now();
        
        // 페이징된 거래 내역 조회
        List<PointTransactionEntity> transactionEntities = userRepository
                .findTransactionsByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                        userEntity.getUserId(), cursor, pageRequest.getSize());
        
        List<PointTransaction> transactions = userMapper.toPointTransactionList(transactionEntities);
        
        // 다음 페이지 존재 여부 확인
        boolean hasNext = false;
        LocalDateTime nextCursor = null;
        
        if (!transactions.isEmpty()) {
            // 마지막 거래의 생성 시간을 다음 커서로 설정
            nextCursor = transactions.get(transactions.size() - 1).getCreatedAt();
            
            // 다음 페이지가 있는지 확인 (마지막 거래보다 이전에 더 많은 거래가 있는지)
            hasNext = userRepository.existsByUserIdAndCreatedAtBefore(
                    userEntity.getUserId(), nextCursor);
        }
        
        return PageResponse.of(transactions, nextCursor, hasNext, pageRequest.getSize());
    }

    /**
     * 포인트 거래 내역을 생성합니다.
     * @param userId 사용자 ID
     * @param transactionType 거래 타입
     * @param amount 거래 금액
     * @param balance 거래 후 잔액
     */
    private void createPointTransaction(Long userId, TransactionType transactionType, int amount, int balance) {
        UserEntity userEntity = findUser(userId);
        PointTransactionEntity transactionEntity = PointTransactionEntity.builder()
                .userId(userEntity.getUserId())
                .transactionType(transactionType)
                .amount(amount)
                .balance(balance)
                .build();
        userRepository.savePointTransaction(transactionEntity);
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_FOUND));
    }
}
