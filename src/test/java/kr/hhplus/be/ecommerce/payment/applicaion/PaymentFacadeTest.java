package kr.hhplus.be.ecommerce.payment.applicaion;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;
import kr.hhplus.be.ecommerce.payment.domain.PaymentService;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "테스트 옵션";
    private static final Integer TEST_QUANTITY = 2;
    private static final Long TEST_UNIT_PRICE = 10000L;
    private static final Long TEST_TOTAL_AMOUNT = 20000L;
    private static final Long TEST_DISCOUNT_AMOUNT = 2000L;
    private static final Long TEST_FINAL_AMOUNT = 18000L;
    private static final Long TEST_USER_BALANCE = 25000L;
    private static final Long TEST_INSUFFICIENT_BALANCE = 10000L;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private CouponService couponService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    private Order order;
    private Payment payment;
    private Payment successPayment;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        orderItems = createOrderItems();
        order = createOrder();
        payment = createPayment();
        successPayment = createSuccessPayment();
    }

    @Nested
    @DisplayName("주문 결제")
    class PayOrder {

        @Test
        @DisplayName("정상적인 결제를 처리한다")
        void payOrder_Success() {
            // given
            when(orderService.getOrder(TEST_USER_ID, TEST_ORDER_ID)).thenReturn(order);
            when(userService.getBalance(TEST_USER_ID)).thenReturn(TEST_USER_BALANCE);
            doNothing().when(userService).usePoint(any(), any());
            when(paymentService.createPayment(any(), any(), any(), any())).thenReturn(payment);
            when(paymentService.processPaymentSuccess(any())).thenReturn(successPayment);
            doNothing().when(orderService).confirmOrder(any(), any());

            // when
            Payment result = paymentFacade.payOrder(TEST_USER_ID, TEST_ORDER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPaymentId()).isEqualTo(TEST_PAYMENT_ID);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

            verify(orderService).getOrder(TEST_USER_ID, TEST_ORDER_ID);
            verify(userService).getBalance(TEST_USER_ID);
            verify(userService).usePoint(TEST_USER_ID, TEST_TOTAL_AMOUNT);
            verify(paymentService).createPayment(TEST_ORDER_ID, TEST_TOTAL_AMOUNT, TEST_DISCOUNT_AMOUNT, TEST_FINAL_AMOUNT);
            verify(paymentService).processPaymentSuccess(TEST_PAYMENT_ID);
            verify(orderService).confirmOrder(TEST_USER_ID, TEST_ORDER_ID);
        }

        @Test
        @DisplayName("포인트가 부족하면 예외가 발생한다")
        void payOrder_InsufficientPoints_ThrowsException() {
            // given
            when(orderService.getOrder(TEST_USER_ID, TEST_ORDER_ID)).thenReturn(order);
            when(userService.getBalance(TEST_USER_ID)).thenReturn(TEST_INSUFFICIENT_BALANCE);
            doNothing().when(orderService).cancelOrder(any(), any());
            doNothing().when(couponService).restoreUserCoupon(any(), any());
            doNothing().when(productService).increaseStock(any(), any());

            // when & then
            assertThatThrownBy(() -> paymentFacade.payOrder(TEST_USER_ID, TEST_ORDER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.INSUFFICIENT_POINT.getCode());
                    });

            verify(orderService).getOrder(TEST_USER_ID, TEST_ORDER_ID);
            verify(userService).getBalance(TEST_USER_ID);
            verify(userService, never()).usePoint(any(), any());
            verify(orderService).cancelOrder(TEST_USER_ID, TEST_ORDER_ID);
            verify(couponService).restoreUserCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService).increaseStock(TEST_PRODUCT_OPTION_ID, TEST_QUANTITY);
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

            verify(orderService).getOrder(TEST_USER_ID, TEST_ORDER_ID);
            verify(userService, never()).getBalance(any());
        }
    }

    private List<OrderItem> createOrderItems() {
        OrderItem orderItem = OrderItem.builder()
                .orderItemId(1L)
                .orderId(TEST_ORDER_ID)
                .productId(TEST_PRODUCT_ID)
                .productOptionId(TEST_PRODUCT_OPTION_ID)
                .productOptionName(TEST_PRODUCT_OPTION_NAME)
                .quantity(TEST_QUANTITY)
                .unitPrice(TEST_UNIT_PRICE)
                .discountAmount(0L)
                .finalPrice(TEST_UNIT_PRICE * TEST_QUANTITY)
                .build();

        return List.of(orderItem);
    }

    private Order createOrder() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(TEST_COUPON_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
    }

    private Order createOrderWithoutCoupon() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(null)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(0L)
                .finalAmount(TEST_TOTAL_AMOUNT)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
    }

    private Payment createPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .originalPrice(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalPrice(TEST_FINAL_AMOUNT)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createSuccessPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .originalPrice(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalPrice(TEST_FINAL_AMOUNT)
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 