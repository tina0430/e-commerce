package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 도메인 서비스 테스트")
class PaymentServiceTest {

    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Integer TEST_TOTAL_AMOUNT = 20000;
    private static final Integer TEST_DISCOUNT_AMOUNT = 2000;
    private static final Integer TEST_FINAL_AMOUNT = 18000;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentPersistenceMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentEntity paymentEntity;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentEntity = createMockPaymentEntity();
        payment = createMockPayment();
    }

    @Nested
    @DisplayName("결제 생성")
    class CreatePayment {

        @Test
        @DisplayName("결제를 정상적으로 생성한다")
        void createPayment_Success() {
            // given
            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(TEST_DISCOUNT_AMOUNT)
                    .finalAmount(TEST_FINAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);
            when(paymentMapper.toPayment(any(PaymentEntity.class))).thenReturn(createMockPayment());

            // when
            Payment result = paymentService.createPayment(TEST_ORDER_ID, TEST_TOTAL_AMOUNT, TEST_DISCOUNT_AMOUNT, TEST_FINAL_AMOUNT);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(result.getDiscountAmount()).isEqualTo(TEST_DISCOUNT_AMOUNT);
            assertThat(result.getFinalAmount()).isEqualTo(TEST_FINAL_AMOUNT);
            assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("할인이 없는 결제를 생성한다")
        void createPayment_WithoutDiscount() {
            // given
            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .paymentId(TEST_PAYMENT_ID)
                    .orderId(TEST_ORDER_ID)
                    .totalAmount(TEST_TOTAL_AMOUNT)
                    .discountAmount(0)
                    .finalAmount(TEST_TOTAL_AMOUNT)
                    .paymentStatus(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);
            when(paymentMapper.toPayment(any(PaymentEntity.class))).thenReturn(createMockPaymentWithoutDiscount());

            // when
            Payment result = paymentService.createPayment(TEST_ORDER_ID, TEST_TOTAL_AMOUNT, 0, TEST_TOTAL_AMOUNT);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
            assertThat(result.getDiscountAmount()).isEqualTo(0);
            assertThat(result.getFinalAmount()).isEqualTo(TEST_TOTAL_AMOUNT);
        }
    }

    @Nested
    @DisplayName("결제 성공 처리")
    class ProcessPaymentSuccess {

        @Test
        @DisplayName("결제를 성공으로 처리한다")
        void processPaymentSuccess_Success() {
            // given
            Payment successPayment = createMockSuccessPayment();
            PaymentEntity updatedPaymentEntity = createMockPaymentEntity();

            when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.of(paymentEntity));
            when(paymentMapper.toPayment(paymentEntity)).thenReturn(payment);
            when(paymentMapper.toPaymentEntity(any(Payment.class))).thenReturn(updatedPaymentEntity);
            when(paymentRepository.save(updatedPaymentEntity)).thenReturn(updatedPaymentEntity);
            when(paymentMapper.toPayment(updatedPaymentEntity)).thenReturn(successPayment);

            // when
            Payment result = paymentService.processPaymentSuccess(TEST_PAYMENT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(result.isSuccessful()).isTrue();

            verify(paymentRepository).findById(TEST_PAYMENT_ID);
            verify(paymentMapper).toPayment(paymentEntity);
            verify(paymentMapper).toPaymentEntity(any(Payment.class));
            verify(paymentRepository).save(updatedPaymentEntity);
            verify(paymentMapper).toPayment(updatedPaymentEntity);
        }

        @Test
        @DisplayName("존재하지 않는 결제 ID로 성공 처리 시 예외가 발생한다")
        void processPaymentSuccess_PaymentNotFound_ThrowsException() {
            // given
            when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.processPaymentSuccess(TEST_PAYMENT_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PAYMENT_NOT_FOUND.getCode());
                    });

            verify(paymentRepository).findById(TEST_PAYMENT_ID);
            verify(paymentMapper, never()).toPayment(any());
            verify(paymentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("주문 ID로 결제 조회")
    class GetPaymentByOrderId {

        @Test
        @DisplayName("주문 ID로 결제를 조회한다")
        void getPaymentByOrderId_Success() {
            // given
            when(paymentRepository.findByOrderId(TEST_ORDER_ID)).thenReturn(Optional.of(paymentEntity));
            when(paymentMapper.toPayment(paymentEntity)).thenReturn(payment);

            // when
            Payment result = paymentService.getPaymentByOrderId(TEST_ORDER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPaymentId()).isEqualTo(TEST_PAYMENT_ID);
            assertThat(result.getOrderId()).isEqualTo(TEST_ORDER_ID);

            verify(paymentRepository).findByOrderId(TEST_ORDER_ID);
            verify(paymentMapper).toPayment(paymentEntity);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 조회 시 예외가 발생한다")
        void getPaymentByOrderId_PaymentNotFound_ThrowsException() {
            // given
            when(paymentRepository.findByOrderId(TEST_ORDER_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.getPaymentByOrderId(TEST_ORDER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PAYMENT_NOT_FOUND.getCode());
                    });

            verify(paymentRepository).findByOrderId(TEST_ORDER_ID);
            verify(paymentMapper, never()).toPayment(any());
        }
    }

    private PaymentEntity createMockPaymentEntity() {
        return PaymentEntity.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PaymentEntity createMockPaymentEntityWithoutDiscount() {
        return PaymentEntity.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(0)
                .finalAmount(TEST_TOTAL_AMOUNT)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createMockPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createMockPaymentWithoutDiscount() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(0)
                .finalAmount(TEST_TOTAL_AMOUNT)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createMockSuccessPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createMockFailedPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalAmount(TEST_FINAL_AMOUNT)
                .paymentStatus(PaymentStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 