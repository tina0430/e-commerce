package kr.hhplus.be.ecommerce.order.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderEntity;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderPersistenceMapper orderMapper;

    /**
     * 주문을 생성합니다.
     * @param userId 사용자 ID
     * @param userCoupon 사용자 쿠폰
     * @param orderItems 주문 요청 정보
     * @return 생성된 주문
     */
    public Order createOrder(Long userId, UserCoupon userCoupon, List<OrderItem> orderItems) {
        Order order = Order.createOrder(userId, userCoupon, orderItems);
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        return orderMapper.toOrder(savedOrderEntity);
    }

    /**
     * 주문을 조회합니다.
     * @param userId 사용자 ID
     * @param orderId 주문 ID
     * @return 주문 정보
     */
    public Order getOrder(Long userId, Long orderId) {
        OrderEntity orderEntity = getOrderEntity(userId, orderId);
        return orderMapper.toOrder(orderEntity);
    }

    /**
     * 사용자의 주문 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    public List<Order> getUserOrders(Long userId) {
        List<OrderEntity> orderEntities = orderRepository.findByUserId(userId);
        return orderMapper.toOrderList(orderEntities);
    }

    /**
     * 주문을 취소합니다.
     * @param orderId 주문 ID
     */
    @Transactional // 사용자 취소 대비
    public void cancelOrder(Long userId, Long orderId) {
        OrderEntity orderEntity = getOrderEntity(userId, orderId);
        Order order = orderMapper.toOrder(orderEntity);
        order.cancel();
        orderMapper.applyToEntity(order, orderEntity);
    }

    /**
     * @param orderId 주문 ID
     */
    public void confirmOrder(Long userId, Long orderId) {
        OrderEntity orderEntity = getOrderEntity(userId, orderId);
        Order order = orderMapper.toOrder(orderEntity);
        order.confirm();
        orderMapper.applyToEntity(order, orderEntity);
    }

    private OrderEntity getOrderEntity(Long userId, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessError.ORDER_NOT_FOUND));
        if (!orderEntity.getUserId().equals(userId)) {
            throw new BusinessException(BusinessError.UNAUTHORIZED_ORDER_ACCESS);
        }
        return orderEntity;
    }
}
