package kr.hhplus.be.ecommerce.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 도메인 객체 테스트")
class PaymentTest {

    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_ORIGINAL_PRICE = 20000L;
    private static final Long TEST_DISCOUNT_AMOUNT = 2000L;
    private static final Long TEST_FINAL_PRICE = 18000L;
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

    @Nested
    @DisplayName("결제 생성")
    class CreatePayment {

        @Test
        @DisplayName("정상적인 결제를 생성한다")
        void createPayment_Success() {
            // when
            Payment payment = createPayment();

            // then
            assertThat(payment.getPaymentId()).isEqualTo(TEST_PAYMENT_ID);
            assertThat(payment.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(payment.getOriginalPrice()).isEqualTo(TEST_ORIGINAL_PRICE);
            assertThat(payment.getDiscountAmount()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(payment.getFinalPrice()).isEqualTo(TEST_FINAL_PRICE);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getCreatedAt()).isEqualTo(TEST_CREATED_AT);
        }

        @Test
        @DisplayName("할인이 없는 결제를 생성한다")
        void createPayment_WithoutDiscount() {
            // when
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .originalPrice(TEST_ORIGINAL_PRICE)
                    .discountAmount(0L)
                    .finalPrice(TEST_ORIGINAL_PRICE)
                    .status(PaymentStatus.PENDING)
                    .createdAt(TEST_CREATED_AT)
                    .build();

            // then
            assertThat(payment.getDiscountAmount()).isEqualTo(0L);
            assertThat(payment.getFinalPrice()).isEqualTo(TEST_ORIGINAL_PRICE);
        }
    }

    @Nested
    @DisplayName("결제 상태 업데이트")
    class UpdatePaymentStatus {

        @Test
        @DisplayName("결제 상태를 성공으로 변경한다")
        void updateStatus_ToSuccess() {
            // given
            Payment payment = createPayment();

            // when
            payment.updateStatus(PaymentStatus.SUCCESS);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @Test
        @DisplayName("결제 상태를 실패로 변경한다")
        void updateStatus_ToFailed() {
            // given
            Payment payment = createPayment();

            // when
            payment.updateStatus(PaymentStatus.FAILED);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("결제 상태를 대기로 변경한다")
        void updateStatus_ToPending() {
            // given
            Payment payment = createPayment();
            payment.updateStatus(PaymentStatus.SUCCESS);

            // when
            payment.updateStatus(PaymentStatus.PENDING);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("결제 상태 확인")
    class CheckPaymentStatus {

        @Test
        @DisplayName("결제가 성공 상태인지 확인한다")
        void isSuccessful_ReturnsTrue() {
            // given
            Payment payment = createPayment();
            payment.updateStatus(PaymentStatus.SUCCESS);

            // when & then
            assertThat(payment.isSuccessful()).isTrue();
        }

        @Test
        @DisplayName("결제가 성공 상태가 아닌지 확인한다")
        void isSuccessful_ReturnsFalse() {
            // given
            Payment payment = createPayment();

            // when & then
            assertThat(payment.isSuccessful()).isFalse();
        }

        @Test
        @DisplayName("결제가 실패 상태인지 확인한다")
        void isFailed_ReturnsTrue() {
            // given
            Payment payment = createPayment();
            payment.updateStatus(PaymentStatus.FAILED);

            // when & then
            assertThat(payment.isFailed()).isTrue();
        }

        @Test
        @DisplayName("결제가 실패 상태가 아닌지 확인한다")
        void isFailed_ReturnsFalse() {
            // given
            Payment payment = createPayment();

            // when & then
            assertThat(payment.isFailed()).isFalse();
        }

        @Test
        @DisplayName("대기 상태의 결제는 성공도 실패도 아니다")
        void pendingPayment_IsNeitherSuccessNorFailed() {
            // given
            Payment payment = createPayment();

            // when & then
            assertThat(payment.isSuccessful()).isFalse();
            assertThat(payment.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("결제 금액 계산")
    class CalculatePaymentAmount {

        @Test
        @DisplayName("할인 금액이 올바르게 계산된다")
        void discountAmount_IsCalculatedCorrectly() {
            // given
            Payment payment = createPayment();

            // when & then
            assertThat(payment.getOriginalPrice() - payment.getDiscountAmount()).isEqualTo(payment.getFinalPrice());
        }

        @Test
        @DisplayName("할인이 없는 경우 원가와 최종가가 같다")
        void noDiscount_OriginalPriceEqualsFinalPrice() {
            // given
            Payment payment = Payment.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .originalPrice(TEST_ORIGINAL_PRICE)
                    .discountAmount(0L)
                    .finalPrice(TEST_ORIGINAL_PRICE)
                    .status(PaymentStatus.PENDING)
                    .createdAt(TEST_CREATED_AT)
                    .build();

            // when & then
            assertThat(payment.getOriginalPrice()).isEqualTo(payment.getFinalPrice());
            assertThat(payment.getDiscountAmount()).isEqualTo(0L);
        }
    }

    private Payment createPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .originalPrice(TEST_ORIGINAL_PRICE)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalPrice(TEST_FINAL_PRICE)
                .status(PaymentStatus.PENDING)
                .createdAt(TEST_CREATED_AT)
                .build();
    }
} 