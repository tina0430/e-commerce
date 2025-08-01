package kr.hhplus.be.ecommerce.user.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hhplus.be.ecommerce.common.infrastructure.JpaRepositoryBase;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.user.domain.model.QPointTransactionEntity.pointTransactionEntity;
import static kr.hhplus.be.ecommerce.user.domain.model.QUserEntity.userEntity;

@Repository
public class JpaUserRepository extends JpaRepositoryBase {

    public JpaUserRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    /**
     * 유저 정보(잔액)
     */
    public Optional<UserEntity> findUserById(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(userEntity)
                        .where(userEntity.userId.eq(userId))
                        .fetchOne());
    }

    public UserEntity saveUserEntity(UserEntity userEntity) {
        return super.save(userEntity);
    }

    /**
     * 포인트 거래내역
     */
    public List<PointTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return queryFactory
                .selectFrom(pointTransactionEntity)
                .where(pointTransactionEntity.userId.eq(userId))
                .orderBy(pointTransactionEntity.createdAt.desc())
                .fetch();
    }

    public List<PointTransactionEntity> findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long userId, LocalDateTime cursor, int size) {
        return queryFactory
                .selectFrom(pointTransactionEntity)
                .where(
                        pointTransactionEntity.userId.eq(userId),
                        pointTransactionEntity.createdAt.lt(cursor)
                )
                .orderBy(pointTransactionEntity.createdAt.desc())
                .limit(size)
                .fetch();
    }

    public boolean existsByUserIdAndCreatedAtBefore(Long userId, LocalDateTime cursor) {
        Integer result = queryFactory
                .selectOne()
                .from(pointTransactionEntity)
                .where(
                        pointTransactionEntity.userId.eq(userId),
                        pointTransactionEntity.createdAt.lt(cursor)
                )
                .fetchFirst(); // 있으면 1, 없으면 null

        return result != null;
    }

    public PointTransactionEntity savePointTransactionEntity(PointTransactionEntity pointTransactionEntity) {
        return super.save(pointTransactionEntity);
    }

} 