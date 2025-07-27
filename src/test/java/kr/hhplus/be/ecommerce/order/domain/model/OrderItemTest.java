package kr.hhplus.be.ecommerce.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 상품 도메인 객체 테스트")
class OrderItemTest {

    private static final Long TEST_ORDER_ITEM_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "테스트 옵션";
    private static final Integer TEST_QUANTITY = 2;
    private static final Long TEST_UNIT_PRICE = 10000L;
    private static final Long TEST_DISCOUNT_AMOUNT = 2000L;
    private static final Long TEST_FINAL_PRICE = 18000L;
    private static final Long TEST_SMALL_DISCOUNT_AMOUNT = 1000L;

    @Nested
    @DisplayName("주문 상품 생성")
    class CreateOrderItem {

        @Test
        @DisplayName("정상적인 주문 상품을 생성한다")
        void createOrderItem_Success() {
            // given & when
            OrderItem orderItem = createOrderItem();

            // then
            assertThat(orderItem.getOrderItemId()).isEqualTo(TEST_ORDER_ITEM_ID);
            assertThat(orderItem.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(orderItem.getProductId()).isEqualTo(TEST_PRODUCT_ID);
            assertThat(orderItem.getProductOptionId()).isEqualTo(TEST_PRODUCT_OPTION_ID);
            assertThat(orderItem.getProductOptionName()).isEqualTo(TEST_PRODUCT_OPTION_NAME);
            assertThat(orderItem.getQuantity()).isEqualTo(TEST_QUANTITY);
            assertThat(orderItem.getUnitPrice()).isEqualTo(TEST_UNIT_PRICE);
            assertThat(orderItem.getDiscountAmount()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(orderItem.getFinalPrice()).isEqualTo(TEST_FINAL_PRICE);
        }
    }

    @Nested
    @DisplayName("상품 가격 계산")
    class CalculatePrice {

        @Test
        @DisplayName("상품의 총 가격을 계산한다")
        void getPrice() {
            // given
            OrderItem orderItem = createOrderItem();

            // when
            Long price = orderItem.getPrice();

            // then
            assertThat(price).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }

        @Test
        @DisplayName("할인이 적용된 최종 가격을 계산한다")
        void getFinalPrice_WithDiscount() {
            // given
            OrderItem orderItem = createOrderItem();

            // when
            Long finalPrice = orderItem.getFinalPrice();

            // then
            assertThat(finalPrice).isEqualTo(TEST_FINAL_PRICE);
        }

        @Test
        @DisplayName("할인이 없는 경우 원가를 반환한다")
        void getFinalPrice_WithoutDiscount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .discountAmount(null)
                    .finalPrice(null)
                    .build();

            // when
            Long finalPrice = orderItem.getFinalPrice();

            // then
            assertThat(finalPrice).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }
    }

    @Nested
    @DisplayName("할인 적용")
    class ApplyDiscount {

        @Test
        @DisplayName("정상적인 할인을 적용한다")
        void applyDiscount_Success() {
            // given
            OrderItem orderItem = createOrderItemWithoutDiscount();

            // when
            orderItem.applyDiscount(TEST_SMALL_DISCOUNT_AMOUNT);

            // then
            assertThat(orderItem.getDiscountAmount()).isEqualTo(TEST_SMALL_DISCOUNT_AMOUNT);
            assertThat(orderItem.getFinalPrice()).isEqualTo((TEST_UNIT_PRICE * TEST_QUANTITY) - TEST_SMALL_DISCOUNT_AMOUNT);
        }

        @Test
        @DisplayName("할인 금액이 상품 가격과 같으면 정상 처리된다")
        void applyDiscount_EqualsPrice() {
            // given
            OrderItem orderItem = createOrderItemWithoutDiscount();
            Long exactDiscountAmount = TEST_UNIT_PRICE * TEST_QUANTITY;

            // when
            orderItem.applyDiscount(exactDiscountAmount);

            // then
            assertThat(orderItem.getDiscountAmount()).isEqualTo(exactDiscountAmount);
            assertThat(orderItem.getFinalPrice()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("할인 정보 확인")
    class DiscountInfo {

        @Test
        @DisplayName("할인이 적용된 상품인지 확인한다")
        void hasDiscount_WithDiscount() {
            // given
            OrderItem orderItem = createOrderItem();

            // when & then
            assertThat(orderItem.getDiscountAmount()).isGreaterThan(0L);
            assertThat(orderItem.getFinalPrice()).isLessThan(orderItem.getPrice());
        }

        @Test
        @DisplayName("할인이 없는 상품인지 확인한다")
        void hasDiscount_WithoutDiscount() {
            // given
            OrderItem orderItem = createOrderItemWithoutDiscount();

            // when & then
            assertThat(orderItem.getDiscountAmount()).isNull();
            assertThat(orderItem.getFinalPrice()).isEqualTo(orderItem.getPrice());
        }
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .orderItemId(TEST_ORDER_ITEM_ID)
                .orderId(TEST_ORDER_ID)
                .productId(TEST_PRODUCT_ID)
                .productOptionId(TEST_PRODUCT_OPTION_ID)
                .productOptionName(TEST_PRODUCT_OPTION_NAME)
                .quantity(TEST_QUANTITY)
                .unitPrice(TEST_UNIT_PRICE)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalPrice(TEST_FINAL_PRICE)
                .build();
    }

    private OrderItem createOrderItemWithoutDiscount() {
        return OrderItem.builder()
                .orderItemId(TEST_ORDER_ITEM_ID)
                .orderId(TEST_ORDER_ID)
                .productId(TEST_PRODUCT_ID)
                .productOptionId(TEST_PRODUCT_OPTION_ID)
                .productOptionName(TEST_PRODUCT_OPTION_NAME)
                .quantity(TEST_QUANTITY)
                .unitPrice(TEST_UNIT_PRICE)
                .discountAmount(null)
                .finalPrice(null)
                .build();
    }
} 