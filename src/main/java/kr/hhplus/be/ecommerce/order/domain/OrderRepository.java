package kr.hhplus.be.ecommerce.order.domain;

import kr.hhplus.be.ecommerce.order.domain.model.OrderEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<OrderEntity> findById(Long orderId);
    List<OrderEntity> findByUserId(Long userId);
    OrderEntity save(OrderEntity orderEntity);

    /**
     * TODO
     * 최근 3일간 많이 팔린 상품을 찾는 메소드
     * @param limit 조회할 상위 상품 개수
     * @return 최근 3일간 판매량이 많은 상품 ID와 판매량 정보
     */
    List<Object[]> findTopSellingProductsInLast3Days(int limit);

    /**
     * TODO
     * 특정 기간 동안 많이 팔린 상품을 찾는 메소드
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param limit 조회할 상위 상품 개수
     * @return 해당 기간 동안 판매량이 많은 상품 ID와 판매량 정보
     */
    List<Object[]> findTopSellingProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit);

}