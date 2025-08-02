package kr.hhplus.be.ecommerce.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 상품 도메인 모델 테스트")
class OrderItemTest {

    private static final Long TEST_ORDER_ITEM_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "테스트 옵션";
    private static final Integer TEST_QUANTITY = 2;
    private static final Integer TEST_UNIT_PRICE = 10000;
    private static final Integer TEST_DISCOUNT_AMOUNT = 1000;
    private static final Integer TEST_FINAL_AMOUNT = 19000;
    private static final Integer TEST_SMALL_DISCOUNT_AMOUNT = 500;

    @Nested
    @DisplayName("OrderItem 생성")
    class OrderItemCreationTests {

        @Test
        @DisplayName("모든 필드가 있는 OrderItem을 생성한다")
        void createOrderItem_WithAllFields() {
            // given & when
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalPrice(TEST_FINAL_AMOUNT)
                    .build();

            // then
            assertThat(orderItem.getOrderItemId()).isEqualTo(TEST_ORDER_ITEM_ID);
            assertThat(orderItem.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(orderItem.getProductId()).isEqualTo(TEST_PRODUCT_ID);
            assertThat(orderItem.getProductOptionId()).isEqualTo(TEST_PRODUCT_OPTION_ID);
            assertThat(orderItem.getProductOptionName()).isEqualTo(TEST_PRODUCT_OPTION_NAME);
            assertThat(orderItem.getQuantity()).isEqualTo(TEST_QUANTITY);
            assertThat(orderItem.getUnitPrice()).isEqualTo(TEST_UNIT_PRICE);
            assertThat(orderItem.getDiscountAmount()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(orderItem.getFinalPrice()).isEqualTo(TEST_FINAL_AMOUNT);
        }

        @Test
        @DisplayName("할인이 없는 OrderItem을 생성한다")
        void createOrderItem_WithoutDiscount() {
            // given & when
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .discountAmount(0)
                    .finalPrice(TEST_UNIT_PRICE * TEST_QUANTITY)
                    .build();

            // then
            Integer price = orderItem.getPrice();
            Integer finalPrice = orderItem.getFinalPrice();
            assertThat(price).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
            assertThat(finalPrice).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }

        @Test
        @DisplayName("최소 필드로 OrderItem을 생성한다")
        void createOrderItem_WithMinimalFields() {
            // given & when
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // then
            Integer finalPrice = orderItem.getFinalPrice();
            assertThat(finalPrice).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }
    }

    @Nested
    @DisplayName("OrderItem 할인 적용")
    class OrderItemDiscountTests {

        @Test
        @DisplayName("할인을 적용한다")
        void applyDiscount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when
            orderItem.applyDiscount(TEST_SMALL_DISCOUNT_AMOUNT);

            // then
            assertThat(orderItem.getDiscountAmount()).isEqualTo(TEST_SMALL_DISCOUNT_AMOUNT);
            assertThat(orderItem.getFinalPrice()).isEqualTo((TEST_UNIT_PRICE * TEST_QUANTITY) - TEST_SMALL_DISCOUNT_AMOUNT);
        }

        @Test
        @DisplayName("정확한 할인을 적용한다")
        void applyDiscount_ExactAmount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when
            Integer exactDiscountAmount = TEST_UNIT_PRICE * TEST_QUANTITY;
            orderItem.applyDiscount(exactDiscountAmount);

            // then
            assertThat(orderItem.getDiscountAmount()).isEqualTo(exactDiscountAmount);
            assertThat(orderItem.getFinalPrice()).isEqualTo(0);
        }

        @Test
        @DisplayName("할인 후 할인 금액이 양수인지 확인한다")
        void applyDiscount_PositiveDiscountAmount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when
            orderItem.applyDiscount(TEST_SMALL_DISCOUNT_AMOUNT);

            // then
            assertThat(orderItem.getDiscountAmount()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("OrderItem 가격 계산")
    class OrderItemPriceCalculationTests {

        @Test
        @DisplayName("가격이 올바르게 계산된다")
        void calculatePrice() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when & then
            assertThat(orderItem.getPrice()).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }

        @Test
        @DisplayName("할인이 적용된 최종 가격이 올바르게 계산된다")
        void calculateFinalPrice_WithDiscount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when
            orderItem.applyDiscount(TEST_DISCOUNT_AMOUNT);

            // then
            assertThat(orderItem.getFinalPrice()).isEqualTo((TEST_UNIT_PRICE * TEST_QUANTITY) - TEST_DISCOUNT_AMOUNT);
        }

        @Test
        @DisplayName("할인이 없는 경우 최종 가격이 원가와 같다")
        void calculateFinalPrice_WithoutDiscount() {
            // given
            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(TEST_ORDER_ITEM_ID)
                    .orderId(TEST_ORDER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .productOptionId(TEST_PRODUCT_OPTION_ID)
                    .productOptionName(TEST_PRODUCT_OPTION_NAME)
                    .quantity(TEST_QUANTITY)
                    .unitPrice(TEST_UNIT_PRICE)
                    .build();

            // when & then
            assertThat(orderItem.getFinalPrice()).isEqualTo(TEST_UNIT_PRICE * TEST_QUANTITY);
        }
    }
} 