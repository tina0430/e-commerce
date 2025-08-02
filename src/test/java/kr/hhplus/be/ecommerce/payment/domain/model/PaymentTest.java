package kr.hhplus.be.ecommerce.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 도메인 모델 테스트")
class PaymentTest {

    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Integer TEST_TOTAL_AMOUNT = 20000;
    private static final Integer TEST_DISCOUNT_AMOUNT = 2000;
    private static final Integer TEST_FINAL_AMOUNT = 18000;

    @Nested
    @DisplayName("Payment 생성")
    class PaymentCreationTests {

        @Test
        @DisplayName("모든 필드가 있는 Payment를 생성한다")
        void createPayment_WithAllFields() {
            // given & when
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(payment.getPaymentId()).isEqualTo(TEST_PAYMENT_ID);
            assertThat(payment.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(payment.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(payment.getDiscountAmount()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(payment.getFinalAmount()).isEqualTo(TEST_FINAL_AMOUNT);
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("할인이 없는 Payment를 생성한다")
        void createPayment_WithoutDiscount() {
            // given & when
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(0)
                    .finalAmount(TEST_TOTAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(payment.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(payment.getFinalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
        }

        @Test
        @DisplayName("최소 필드로 Payment를 생성한다")
        void createPayment_WithMinimalFields() {
            // given & when
            Payment payment = Payment.builder().build();

            // then
            assertThat(payment.getPaymentId()).isNull();
            assertThat(payment.getOrderId()).isNull();
            assertThat(payment.getTotalAmount()).isNull();
            assertThat(payment.getDiscountAmount()).isNull();
            assertThat(payment.getFinalAmount()).isNull();
            assertThat(payment.getPaymentStatus()).isNull();
            assertThat(payment.getCreatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Payment 상태 관리")
    class PaymentStatusTests {

        @Test
        @DisplayName("결제 상태를 성공으로 변경한다")
        void updateStatus_Success() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when
            payment.updateStatus(PaymentStatus.SUCCESS);

            // then
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @Test
        @DisplayName("결제 상태를 실패로 변경한다")
        void updateStatus_Failed() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when
            payment.updateStatus(PaymentStatus.FAILED);

            // then
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("Payment 상태 확인")
    class PaymentStatusCheckTests {

        @Test
        @DisplayName("성공 상태인지 확인한다")
        void isSuccessful() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.isSuccessful()).isTrue();
        }

        @Test
        @DisplayName("실패 상태인지 확인한다")
        void isFailed() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.FAILED)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.isFailed()).isTrue();
        }

        @Test
        @DisplayName("대기 상태는 성공이 아니다")
        void isSuccessful_PendingStatus() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.isSuccessful()).isFalse();
        }
    }

    @Nested
    @DisplayName("Payment 금액 계산")
    class PaymentAmountCalculationTests {

        @Test
        @DisplayName("할인이 적용된 금액이 올바르게 계산된다")
        void calculateAmount_WithDiscount() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.getTotalAmount() - payment.getDiscountAmount()).isEqualTo(payment.getFinalAmount());
        }

        @Test
        @DisplayName("할인이 없는 경우 총액과 최종액이 같다")
        void calculateAmount_WithoutDiscount() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(0)
                    .finalAmount(TEST_TOTAL_AMOUNT)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.getTotalAmount()).isEqualTo(payment.getFinalAmount());
        }

        @Test
        @DisplayName("최대 할인이 적용된 경우")
        void calculateAmount_WithMaximumDiscount() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_TOTAL_AMOUNT)
                    .finalAmount(0)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            // when & then
            assertThat(payment.getFinalAmount()).isEqualTo(0);
        }
    }
} 