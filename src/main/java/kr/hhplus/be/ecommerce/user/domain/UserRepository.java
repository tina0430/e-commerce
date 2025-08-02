package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * 사용자
     */
    Optional<UserEntity> findUserById(Long userId);
    UserEntity saveUser(UserEntity userEntity);

    /**
     * 포인트 거래 내역
     */
    List<PointTransactionEntity> findTransactionsByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 포인트 거래 내역 페이징 조회 (날짜 기준)
     */
    List<PointTransactionEntity> findTransactionsByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long userId, LocalDateTime cursor, int size);

    /**
     * 다음 페이지 존재 여부 확인
     */
    boolean existsByUserIdAndCreatedAtBefore(Long userId, LocalDateTime cursor);

    PointTransactionEntity savePointTransaction(PointTransactionEntity pointTransactionEntity);
}