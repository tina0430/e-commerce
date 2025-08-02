package kr.hhplus.be.ecommerce.payment.applicaion;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;
import kr.hhplus.be.ecommerce.payment.domain.PaymentService;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 애플리케이션 레이어 테스트")
class PaymentFacadeTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long TEST_COUPON_ID = 1L;
    private static final Integer TEST_TOTAL_AMOUNT = 20000;
    private static final Integer TEST_DISCOUNT_AMOUNT = 2000;
    private static final Integer TEST_FINAL_AMOUNT = 18000;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Nested
    @DisplayName("주문 결제")
    class PayOrder {

        @Test
        @DisplayName("정상적인 결제를 처리한다")
        void payOrder_Success() {
            // given
            Order mockOrder = createMockOrder();
            Payment mockPayment = createMockPayment(PaymentStatus.PENDING);
            Payment mockSuccessPayment = createMockPayment(PaymentStatus.SUCCESS);

            when(orderService.getOrder(TEST_USER_ID, TEST_ORDER_ID)).thenReturn(mockOrder);
            when(paymentService.createPayment(anyLong(), anyInt(), anyInt(), anyInt())).thenReturn(mockPayment);
            when(paymentService.processPaymentSuccess(anyLong())).thenReturn(mockSuccessPayment);

            // when
            Payment result = paymentFacade.payOrder(TEST_USER_ID, TEST_ORDER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @Test
        @DisplayName("주문이 존재하지 않으면 예외가 발생한다")
        void payOrder_OrderNotFound_ThrowsException() {
            // given
            when(orderService.getOrder(TEST_USER_ID, TEST_ORDER_ID))
                    .thenThrow(new BusinessException(BusinessError.ORDER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> paymentFacade.payOrder(TEST_USER_ID, TEST_ORDER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.ORDER_NOT_FOUND.getCode());
                    });

            verify(userService, never()).getCurrentBalance(anyLong());
        }
    }

    private Order createMockOrder() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(TEST_COUPON_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Payment createMockPayment(PaymentStatus status) {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .paymentStatus(status)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 