package kr.hhplus.be.ecommerce.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 상태 enum 테스트")
class PaymentStatusTest {

    @Test
    @DisplayName("결제 상태 enum 값들이 올바르게 정의되어 있다")
    void paymentStatus_ValuesAreCorrectlyDefined() {
        // when & then
        assertThat(PaymentStatus.values()).hasSize(3);
        assertThat(PaymentStatus.PENDING).isNotNull();
        assertThat(PaymentStatus.SUCCESS).isNotNull();
        assertThat(PaymentStatus.FAILED).isNotNull();
    }

    @Test
    @DisplayName("결제 상태 enum 값들이 올바른 순서로 정의되어 있다")
    void paymentStatus_ValuesAreInCorrectOrder() {
        // when & then
        PaymentStatus[] values = PaymentStatus.values();
        assertThat(values[0]).isEqualTo(PaymentStatus.PENDING);
        assertThat(values[1]).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(values[2]).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("결제 상태 enum 값들이 올바른 이름을 가지고 있다")
    void paymentStatus_ValuesHaveCorrectNames() {
        // when & then
        assertThat(PaymentStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(PaymentStatus.SUCCESS.name()).isEqualTo("SUCCESS");
        assertThat(PaymentStatus.FAILED.name()).isEqualTo("FAILED");
    }

    @Test
    @DisplayName("결제 상태 enum 값들이 올바른 ordinal을 가지고 있다")
    void paymentStatus_ValuesHaveCorrectOrdinals() {
        // when & then
        assertThat(PaymentStatus.PENDING.ordinal()).isEqualTo(0);
        assertThat(PaymentStatus.SUCCESS.ordinal()).isEqualTo(1);
        assertThat(PaymentStatus.FAILED.ordinal()).isEqualTo(2);
    }

    @Test
    @DisplayName("결제 상태 enum 값들이 서로 다른 값을 가지고 있다")
    void paymentStatus_ValuesAreDistinct() {
        // when & then
        assertThat(PaymentStatus.PENDING).isNotEqualTo(PaymentStatus.SUCCESS);
        assertThat(PaymentStatus.PENDING).isNotEqualTo(PaymentStatus.FAILED);
        assertThat(PaymentStatus.SUCCESS).isNotEqualTo(PaymentStatus.FAILED);
    }
} 