package kr.hhplus.be.ecommerce.order.application;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.common.exception.SystemError;
import kr.hhplus.be.ecommerce.common.exception.SystemException;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponStatus;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 애플리케이션 레이어 테스트")
class OrderFacadeTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_COUPON_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "테스트 옵션";
    private static final Integer TEST_QUANTITY = 2;
    private static final Long TEST_UNIT_PRICE = 10000L;
    private static final Long TEST_TOTAL_AMOUNT = 20000L;
    private static final Long TEST_DISCOUNT_VALUE = 2000L;
    private static final Long TEST_MAX_DISCOUNT_AMOUNT = 2000L;
    private static final Long TEST_MIN_ORDER_AMOUNT = 10000L;

    @Mock
    private OrderService orderService;

    @Mock
    private CouponService couponService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderFacade orderFacade;

    private List<OrderItem> orderItems;
    private UserCoupon userCoupon;
    private Order order;

    @BeforeEach
    void setUp() {
        orderItems = createOrderItems();
        userCoupon = createUserCoupon();
        order = createOrder();
    }

    @Nested
    @DisplayName("상품 주문")
    class OrderProducts {

        @Test
        @DisplayName("정상적인 주문을 처리한다")
        void orderProducts_Success() {
            // given
            doNothing().when(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            doNothing().when(productService).validateAndReduceStock(orderItems);
            when(couponService.useUserCoupon(TEST_COUPON_ID, TEST_USER_ID)).thenReturn(userCoupon);
            when(orderService.createOrder(eq(TEST_USER_ID), eq(userCoupon), eq(orderItems))).thenReturn(order);

            // when
            Order result = orderFacade.orderProducts(TEST_USER_ID, TEST_COUPON_ID, orderItems);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);

            verify(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService).validateAndReduceStock(orderItems);
            verify(couponService).useUserCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(orderService).createOrder(TEST_USER_ID, userCoupon, orderItems);
        }

        @Test
        @DisplayName("쿠폰이 유효하지 않으면 예외가 발생한다")
        void orderProducts_InvalidCoupon_ThrowsException() {
            // given
            doThrow(new BusinessException(BusinessError.USER_COUPON_NOT_AVAILABLE))
                    .when(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);

            // when & then
            assertThatThrownBy(() -> orderFacade.orderProducts(TEST_USER_ID, TEST_COUPON_ID, orderItems))
                    .isInstanceOf(BusinessException.class);

            verify(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService, never()).validateAndReduceStock(any());
            verify(couponService, never()).useUserCoupon(any(), any());
            verify(orderService, never()).createOrder(any(), any(), any());
        }

        @Test
        @DisplayName("재고가 부족하면 예외가 발생한다")
        void orderProducts_InsufficientStock_ThrowsException() {
            // given
            doNothing().when(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            doThrow(new BusinessException(BusinessError.PRODUCT_OPTION_OUT_OF_STOCK))
                    .when(productService).validateAndReduceStock(orderItems);

            // when & then
            assertThatThrownBy(() -> orderFacade.orderProducts(TEST_USER_ID, TEST_COUPON_ID, orderItems))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_OPTION_OUT_OF_STOCK.getCode());
                    });

            verify(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService).validateAndReduceStock(orderItems);
            verify(couponService, never()).useUserCoupon(any(), any());
            verify(orderService, never()).createOrder(any(), any(), any());
        }

        @Test
        @DisplayName("쿠폰 사용 중 오류가 발생하면 예외가 발생한다")
        void orderProducts_CouponUseError_ThrowsException() {
            // given
            doNothing().when(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            doNothing().when(productService).validateAndReduceStock(orderItems);
            doThrow(new BusinessException(BusinessError.USER_COUPON_NOT_AVAILABLE))
                    .when(couponService).useUserCoupon(TEST_COUPON_ID, TEST_USER_ID);

            // when & then
            assertThatThrownBy(() -> orderFacade.orderProducts(TEST_USER_ID, TEST_COUPON_ID, orderItems))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.USER_COUPON_NOT_AVAILABLE.getCode());
                    });

            verify(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService).validateAndReduceStock(orderItems);
            verify(couponService).useUserCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(orderService, never()).createOrder(any(), any(), any());
        }

        @Test
        @DisplayName("주문 생성 중 오류가 발생하면 시스템 예외가 발생한다")
        void orderProducts_OrderCreationError_ThrowsSystemException() {
            // given
            doNothing().when(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            doNothing().when(productService).validateAndReduceStock(orderItems);
            when(couponService.useUserCoupon(TEST_COUPON_ID, TEST_USER_ID)).thenReturn(userCoupon);
            doThrow(new RuntimeException("주문 생성 실패"))
                    .when(orderService).createOrder(eq(TEST_USER_ID), eq(userCoupon), eq(orderItems));

            // when & then
            assertThatThrownBy(() -> orderFacade.orderProducts(TEST_USER_ID, TEST_COUPON_ID, orderItems))
                    .isInstanceOf(SystemException.class)
                    .satisfies(exception -> {
                        SystemException systemException = (SystemException) exception;
                        assertThat(systemException.getCode()).isEqualTo(SystemError.UNKNOWN_ERROR.getCode());
                    });

            verify(couponService).validateCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(productService).validateAndReduceStock(orderItems);
            verify(couponService).useUserCoupon(TEST_COUPON_ID, TEST_USER_ID);
            verify(orderService).createOrder(TEST_USER_ID, userCoupon, orderItems);
        }

        @Test
        @DisplayName("쿠폰이 없는 주문을 처리한다")
        void orderProducts_WithoutCoupon() {
            // given
            doNothing().when(couponService).validateCoupon(null, TEST_USER_ID);
            doNothing().when(productService).validateAndReduceStock(orderItems);
            when(couponService.useUserCoupon(null, TEST_USER_ID)).thenReturn(null);
            when(orderService.createOrder(eq(TEST_USER_ID), eq(null), eq(orderItems))).thenReturn(order);

            // when
            Order result = orderFacade.orderProducts(TEST_USER_ID, null, orderItems);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);

            verify(couponService).validateCoupon(null, TEST_USER_ID);
            verify(productService).validateAndReduceStock(orderItems);
            verify(couponService).useUserCoupon(null, TEST_USER_ID);
            verify(orderService).createOrder(TEST_USER_ID, null, orderItems);
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

    private UserCoupon createUserCoupon() {
        return UserCoupon.builder()
                .userCouponId(TEST_COUPON_ID)
                .couponPolicyId(1L)
                .userId(TEST_USER_ID)
                .couponName("테스트 쿠폰")
                .discountType(DiscountType.AMOUNT)
                .discountValue(TEST_DISCOUNT_VALUE)
                .maxDiscountAmount(TEST_MAX_DISCOUNT_AMOUNT)
                .minOrderAmount(TEST_MIN_ORDER_AMOUNT)
                .status(UserCouponStatus.AVAILABLE)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Order createOrder() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(TEST_COUPON_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(0L)
                .finalAmount(TEST_TOTAL_AMOUNT)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
    }
} 