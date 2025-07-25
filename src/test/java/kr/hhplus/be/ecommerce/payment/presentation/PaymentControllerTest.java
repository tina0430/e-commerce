package kr.hhplus.be.ecommerce.payment.presentation;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.common.exception.GlobalExceptionHandler;
import kr.hhplus.be.ecommerce.payment.applicaion.PaymentFacade;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 컨트롤러 테스트")
class PaymentControllerTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long TEST_ORIGINAL_PRICE = 20000L;
    private static final Long TEST_DISCOUNT_AMOUNT = 2000L;
    private static final Integer TEST_FINAL_PRICE = 18000;
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

    @Mock
    private PaymentFacade paymentFacade;

    @Mock
    private PaymentDtoMapper paymentMapper;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private Payment payment;
    private PaymentDto.PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        payment = createPayment();
        paymentResponse = createPaymentResponse();
    }

    @Nested
    @DisplayName("주문 결제")
    class PayOrder {

        @Test
        @DisplayName("정상적인 결제를 처리한다")
        void payOrder_Success() throws Exception {
            // given
            when(paymentFacade.payOrder(eq(TEST_USER_ID), eq(TEST_ORDER_ID))).thenReturn(payment);
            when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);

            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", TEST_USER_ID, TEST_ORDER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.paymentId").value(TEST_PAYMENT_ID))
                    .andExpect(jsonPath("$.originalPrice").value(TEST_ORIGINAL_PRICE))
                    .andExpect(jsonPath("$.discountAmount").value(TEST_DISCOUNT_AMOUNT))
                    .andExpect(jsonPath("$.finalPrice").value(TEST_FINAL_PRICE))
                    .andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.name()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("주문이 존재하지 않으면 404 에러를 반환한다")
        void payOrder_OrderNotFound_Returns404() throws Exception {
            // given
            when(paymentFacade.payOrder(eq(TEST_USER_ID), eq(TEST_ORDER_ID)))
                    .thenThrow(new BusinessException(BusinessError.ORDER_NOT_FOUND));

            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", TEST_USER_ID, TEST_ORDER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(BusinessError.ORDER_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(BusinessError.ORDER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("포인트가 부족하면 400 에러를 반환한다")
        void payOrder_InsufficientPoints_Returns400() throws Exception {
            // given
            when(paymentFacade.payOrder(eq(TEST_USER_ID), eq(TEST_ORDER_ID)))
                    .thenThrow(new BusinessException(BusinessError.INSUFFICIENT_POINT));

            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", TEST_USER_ID, TEST_ORDER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(BusinessError.INSUFFICIENT_POINT.getCode()))
                    .andExpect(jsonPath("$.message").value(BusinessError.INSUFFICIENT_POINT.getMessage()));
        }

        @Test
        @DisplayName("결제 처리 중 오류가 발생하면 500 에러를 반환한다")
        void payOrder_PaymentError_Returns500() throws Exception {
            // given
            when(paymentFacade.payOrder(eq(TEST_USER_ID), eq(TEST_ORDER_ID)))
                    .thenThrow(new BusinessException(BusinessError.UNKNOWN_ERROR));

            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", TEST_USER_ID, TEST_ORDER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(BusinessError.UNKNOWN_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(BusinessError.UNKNOWN_ERROR.getMessage()));
        }

        @Test
        @DisplayName("잘못된 사용자 ID 형식이면 400 에러를 반환한다")
        void payOrder_InvalidUserId_Returns400() throws Exception {
            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", "invalid", TEST_ORDER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("잘못된 주문 ID 형식이면 400 에러를 반환한다")
        void payOrder_InvalidOrderId_Returns400() throws Exception {
            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders/{orderId}/payment", TEST_USER_ID, "invalid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    private Payment createPayment() {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .originalPrice(TEST_ORIGINAL_PRICE)
                .discountAmount(TEST_DISCOUNT_AMOUNT)
                .finalPrice(TEST_FINAL_PRICE.longValue())
                .status(PaymentStatus.SUCCESS)
                .createdAt(TEST_CREATED_AT)
                .build();
    }

    private PaymentDto.PaymentResponse createPaymentResponse() {
        return new PaymentDto.PaymentResponse(
                TEST_PAYMENT_ID,
                TEST_ORIGINAL_PRICE,
                TEST_DISCOUNT_AMOUNT,
                TEST_FINAL_PRICE,
                PaymentStatus.SUCCESS,
                TEST_CREATED_AT
        );
    }
} 