package kr.hhplus.be.ecommerce.order.domain.model;

import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 도메인 모델 테스트")
class OrderTest {

    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_USER_COUPON_ID = 1L;
    private static final Integer TEST_TOTAL_AMOUNT = 20000;
    private static final Integer TEST_DISCOUNT_AMOUNT = 2000;
    private static final Integer TEST_FINAL_AMOUNT = 18000;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "테스트 옵션";
    private static final Integer TEST_QUANTITY = 2;
    private static final Integer TEST_UNIT_PRICE = 10000;
    private static final Integer TEST_ITEM_DISCOUNT_AMOUNT = 1000;
    private static final Integer TEST_ITEM_FINAL_AMOUNT = 19000;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("정상적인 주문을 생성한다")
        void createOrder_Success() {
            // given
            List<OrderItem> orderItems = createOrderItems();

            // when
            Order order = Order.createOrder(TEST_USER_ID, null, orderItems);

            // then
            assertThat(order.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(order.getUserCouponId()).isNull();
            assertThat(order.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getOrderItems()).hasSize(1);
            assertThat(order.getCreatedAt()).isNotNull();
            assertThat(order.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("쿠폰이 있는 주문을 생성한다")
        void createOrder_WithCoupon() {
            // given
            List<OrderItem> orderItems = createOrderItems();
            UserCoupon userCoupon = createUserCoupon();

            // when
            Order order = Order.createOrder(TEST_USER_ID, userCoupon, orderItems);

            // then
            assertThat(order.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(order.getUserCouponId()).isEqualTo(TEST_USER_COUPON_ID);
            assertThat(order.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getOrderItems()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class UpdateOrderStatus {

        @Test
        @DisplayName("주문 상태를 업데이트한다")
        void updateStatus_Success() {
            // given
            Order order = createOrder();

            // when
            order.updateStatus(OrderStatus.CONFIRMED);

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        }
    }

    @Nested
    @DisplayName("주문 취소")
    class CancelOrder {

        @Test
        @DisplayName("대기 중인 주문을 취소한다")
        void cancel_PendingOrder() {
            // given
            Order order = createOrder();
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);

            // when
            order.cancel();

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
        }

        @Test
        @DisplayName("확정된 주문을 취소한다")
        void cancel_ConfirmedOrder() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.CONFIRMED);

            // when
            order.cancel();

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("배송 중인 주문은 취소할 수 없다")
        void cancel_ShippingOrder_ThrowsException() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.SHIPPING);

            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("취소할 수 없는 주문 상태입니다.");
        }

        @Test
        @DisplayName("배송 완료된 주문은 취소할 수 없다")
        void cancel_DeliveredOrder_ThrowsException() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.DELIVERED);

            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("취소할 수 없는 주문 상태입니다.");
        }
    }

    @Nested
    @DisplayName("주문 확정")
    class ConfirmOrder {

        @Test
        @DisplayName("대기 중인 주문을 확정한다")
        void confirm_PendingOrder() {
            // given
            Order order = createOrder();
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);

            // when
            order.confirm();

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
        }

        @Test
        @DisplayName("이미 확정된 주문은 다시 확정할 수 없다")
        void confirm_AlreadyConfirmedOrder_ThrowsException() {
            // given
            Order order = createOrder();
            order.confirm();

            // when & then
            assertThatThrownBy(order::confirm)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("확정할 수 없는 주문 상태입니다.");
        }

        @Test
        @DisplayName("배송 중인 주문은 확정할 수 없다")
        void confirm_ShippingOrder_ThrowsException() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.SHIPPING);

            // when & then
            assertThatThrownBy(order::confirm)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("확정할 수 없는 주문 상태입니다.");
        }
    }

    @Nested
    @DisplayName("주문 상태 확인")
    class CheckOrderStatus {

        @Test
        @DisplayName("주문이 대기 중인지 확인한다")
        void isPending() {
            // given
            Order order = createOrder();

            // when & then
            assertThat(order.isPending()).isTrue();
            assertThat(order.isConfirmed()).isFalse();
            assertThat(order.isShipped()).isFalse();
            assertThat(order.isDelivered()).isFalse();
            assertThat(order.isCancelled()).isFalse();
            assertThat(order.isRefunded()).isFalse();
        }

        @Test
        @DisplayName("주문이 확정되었는지 확인한다")
        void isConfirmed() {
            // given
            Order order = createOrder();
            order.confirm();

            // when & then
            assertThat(order.isConfirmed()).isTrue();
            assertThat(order.isPending()).isFalse();
        }

        @Test
        @DisplayName("주문이 취소되었는지 확인한다")
        void isCancelled() {
            // given
            Order order = createOrder();
            order.cancel();

            // when & then
            assertThat(order.isCancelled()).isTrue();
            assertThat(order.isPending()).isFalse();
        }
    }

    @Nested
    @DisplayName("주문 취소 가능 여부 확인")
    class CanBeCancelled {

        @Test
        @DisplayName("대기 중인 주문은 취소할 수 있다")
        void canBeCancelled_PendingOrder() {
            // given
            Order order = createOrder();

            // when & then
            assertThat(order.canBeCancelled()).isTrue();
        }

        @Test
        @DisplayName("확정된 주문은 취소할 수 있다")
        void canBeCancelled_ConfirmedOrder() {
            // given
            Order order = createOrder();
            order.confirm();

            // when & then
            assertThat(order.canBeCancelled()).isTrue();
        }

        @Test
        @DisplayName("배송 중인 주문은 취소할 수 없다")
        void canBeCancelled_ShippingOrder() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.SHIPPING);

            // when & then
            assertThat(order.canBeCancelled()).isFalse();
        }

        @Test
        @DisplayName("배송 완료된 주문은 취소할 수 없다")
        void canBeCancelled_DeliveredOrder() {
            // given
            Order order = createOrder();
            order.updateStatus(OrderStatus.DELIVERED);

            // when & then
            assertThat(order.canBeCancelled()).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 여부 확인")
    class HasCoupon {

        @Test
        @DisplayName("쿠폰이 있는 주문인지 확인한다")
        void hasCoupon_WithCoupon() {
            // given
            Order order = createOrderWithCoupon();

            // when & then
            assertThat(order.hasCoupon()).isTrue();
        }

        @Test
        @DisplayName("쿠폰이 없는 주문인지 확인한다")
        void hasCoupon_WithoutCoupon() {
            // given
            Order order = createOrder();

            // when & then
            assertThat(order.hasCoupon()).isFalse();
        }
    }

    @Nested
    @DisplayName("총 주문 금액 계산")
    class CalculateTotalAmount {

        @Test
        @DisplayName("주문 상품들의 총 금액을 계산한다")
        void calculateTotalAmount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(1L)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .discountAmount(0)
                    .finalPrice(TEST_UNIT_PRICE * TEST_QUANTITY)
                    .build();

            Order order = Order.builder()
                    .orderId(TEST_ORDER_ID)
                    .userId(TEST_USER_ID)
                    .userCouponId(null)
                    .totalAmount(0)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .orderStatus(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .orderItems(List.of(orderItem))
                    .build();

            // when
            order.calculateTotalAmount();

            // then
            assertThat(order.getTotalAmount()).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }
    }

    private Order createOrder() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(null)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(createOrderItems())
                .build();
    }

    private Order createOrderWithCoupon() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(TEST_USER_COUPON_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(createOrderItems())
                .build();
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
                .discountAmount(TEST_ITEM_DISCOUNT_AMOUNT)
                .finalPrice(TEST_ITEM_FINAL_AMOUNT)
                .build();

        return List.of(orderItem);
    }

    private UserCoupon createUserCoupon() {
        return UserCoupon.builder()
                .userCouponId(TEST_USER_COUPON_ID)
                .couponPolicyId(1L)
                .userId(TEST_USER_ID)
                .couponName("테스트 쿠폰")
                .discountType(DiscountType.AMOUNT)
                .discountValue(2000)
                .maxDiscountAmount(2000)
                .minOrderAmount(10000)
                .usageStatus(UserCouponStatus.AVAILABLE)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 