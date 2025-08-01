package kr.hhplus.be.ecommerce.user.infrastructure;

import kr.hhplus.be.ecommerce.user.domain.UserRepository;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImp implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    /**
     * 사용자
     */
    @Override
    public Optional<UserEntity> findUserById(Long userId) {
        return jpaUserRepository.findUserById(userId);
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        return jpaUserRepository.saveUserEntity(userEntity);
    }

    /**
     * 포인트 거래 내역
     */
    @Override
    public List<PointTransactionEntity> findTransactionsByUserIdOrderByCreatedAtDesc(Long userId) {
        return jpaUserRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<PointTransactionEntity> findTransactionsByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long userId, LocalDateTime cursor, int size) {
        return jpaUserRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, cursor, size);
    }

    @Override
    public boolean existsByUserIdAndCreatedAtBefore(Long userId, LocalDateTime cursor) {
        return jpaUserRepository.existsByUserIdAndCreatedAtBefore(userId, cursor);
    }

    @Override
    public PointTransactionEntity savePointTransaction(PointTransactionEntity pointTransactionEntity) {
        return jpaUserRepository.savePointTransactionEntity(pointTransactionEntity);
    }
}