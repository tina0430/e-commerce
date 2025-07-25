package kr.hhplus.be.ecommerce.order.infrastructure;

import kr.hhplus.be.ecommerce.order.domain.OrderRepository;
import kr.hhplus.be.ecommerce.order.domain.model.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImp implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Optional<OrderEntity> findById(Long orderId) {
        return jpaOrderRepository.findById(orderId);
    }

    @Override
    public List<OrderEntity> findByUserId(Long userId) {
        return jpaOrderRepository.findByUserId(userId);
    }

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        return jpaOrderRepository.save(orderEntity);
    }

    @Override
    public List<Object[]> findTopSellingProductsInLast3Days(int limit) {
        // todo
        return List.of();
    }

    @Override
    public List<Object[]> findTopSellingProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        // todo
        return List.of();
    }
} 