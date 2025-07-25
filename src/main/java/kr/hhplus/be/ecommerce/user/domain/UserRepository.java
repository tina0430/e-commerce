package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * 사용자
     */
    Optional<UserEntity> findUserById(Long userId);
    UserEntity save(UserEntity userEntity);

    /**
     * 포인트 거래 내역
     */
    List<PointTransactionEntity> findTransactionsByUserIdOrderByCreatedAtDesc(Long userId);
    PointTransactionEntity save(PointTransactionEntity pointTransactionEntity);
}