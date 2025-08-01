package kr.hhplus.be.ecommerce.payment.applicaion;

import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.common.exception.SystemError;
import kr.hhplus.be.ecommerce.common.exception.SystemException;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.payment.domain.PaymentService;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserService userService;
    private final CouponService couponService;
    private final ProductService productService;

    @Transactional
    public Payment payOrder(Long userId, Long orderId) {
        log.info("결제 처리 시작 - userId: {}, orderId: {}", userId, orderId);
        Order order = orderService.getOrder(userId, orderId);
        try {
            Integer requiredAmount = order.getTotalAmount();
            userService.usePoint(userId, requiredAmount);
            log.info("포인트 차감 완료 - userId: {}, amount: {}", userId, requiredAmount);
            Payment payment = paymentService.createPayment(orderId, order.getTotalAmount(), order.getDiscountAmount(), order.getFinalAmount());
            log.info("결제 내역 저장 완료 - paymentId: {}", payment.getPaymentId());
            Payment successPayment = paymentService.processPaymentSuccess(payment.getPaymentId());
            // todo 외부 데이터 플랫폼에 결제 데이터 전송
            orderService.confirmOrder(order.getUserId(), order.getOrderId());
            log.info("결제 처리 완료 - userId: {}, orderId: {}, paymentId: {}", userId, orderId, successPayment.getPaymentId());
            return successPayment;
        } catch (BusinessException be) {
            handlePaymentFailure(order);
            throw be;
        } catch (Exception e) {
            handlePaymentFailure(order);
            String message = String.format("결제 처리 중 오류 발생 - userId: %d, orderId: %d, error: %s", userId, orderId, e.getMessage());
            log.error(message, e);
            throw new SystemException(SystemError.UNKNOWN_ERROR, message);
        }
    }

    /**
     * 결제 실패 시 보상 트랜잭션 처리
     * @param order 실패한 주문
     */
    private void handlePaymentFailure(Order order) {
        log.info("결제 실패 보상 트랜잭션 시작 - orderId: {}", order.getOrderId());

        try {
            orderService.cancelOrder(order.getUserId(), order.getOrderId());
            if (order.hasCoupon()) {
                couponService.restoreUserCoupon(order.getUserCouponId(), order.getUserId());
            }
            for (OrderItem orderItem : order.getOrderItems()) {
                productService.increaseStock(
                    orderItem.getProductOptionId(),
                    orderItem.getQuantity()
                );
            }
            orderService.confirmOrder(order.getUserId(), order.getOrderId());
            log.info("결제 실패 보상 트랜잭션 완료 - orderId: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("결제 실패 보상 트랜잭션 중 오류 발생 - orderId: {}, error: {}", order.getOrderId(), e.getMessage());
            // 보상 트랜잭션 실패는 로깅만 하고 상위로 전파하지 않음
            // todo alertService.sendToSlack("보상 트랜잭션 실패", order.getOrderId(), e); 구현
        }
    }
}
