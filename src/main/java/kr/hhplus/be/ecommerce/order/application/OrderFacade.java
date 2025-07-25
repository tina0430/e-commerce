package kr.hhplus.be.ecommerce.order.application;

import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.common.exception.SystemError;
import kr.hhplus.be.ecommerce.common.exception.SystemException;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final CouponService couponService;
    private final ProductService productService;

    @Transactional
    public Order orderProducts(Long userId, Long couponId, List<OrderItem> items) {
        log.info("주문 처리 시작 - userId: {}, items: {}", userId, items);
        try {
            couponService.validateCoupon(couponId, userId);
            productService.validateAndReduceStock(items);
            UserCoupon userCoupon = couponService.useUserCoupon(couponId, userId);
            Order order = orderService.createOrder(userId, userCoupon, items);
            log.info("주문 처리 완료 - orderId: {}", order.getOrderId());
            return order;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String message = String.format("주문 처리 중 오류 발생 - userId: %d, error: %s", userId, e.getMessage());
            log.error(message, e);
            throw new SystemException(SystemError.UNKNOWN_ERROR, message);
        }
    }

}
