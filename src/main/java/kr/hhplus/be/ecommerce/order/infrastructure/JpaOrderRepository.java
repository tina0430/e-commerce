package kr.hhplus.be.ecommerce.order.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.ecommerce.common.infrastructure.JpaRepositoryBase;
import kr.hhplus.be.ecommerce.order.domain.model.OrderEntity;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.order.domain.model.QOrderEntity.orderEntity;

@Repository
public class JpaOrderRepository extends JpaRepositoryBase {

    public JpaOrderRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    public Optional<OrderEntity> findById(Long orderId) {
        return Optional.ofNullable(entityManager.find(OrderEntity.class, orderId));
    }

    public List<OrderEntity> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(orderEntity)
                .where(orderEntity.userId.eq(userId))
                .fetch();
    }

    public OrderEntity save(OrderEntity orderEntity) {
        return super.save(orderEntity);
    }

    public List<OrderEntity> findByStatusAndUserId(OrderStatus status, Long userId) {
        return queryFactory
                .selectFrom(orderEntity)
                .where(orderEntity.orderStatus.eq(status).and(orderEntity.userId.eq(userId)))
                .fetch();
    }
}