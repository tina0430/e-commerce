package kr.hhplus.be.ecommerce.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.TransactionType;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("사용자 컨트롤러 단위 테스트")
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    // 테스트 상수 정의
    private static final Long TEST_USER_ID = 1L;
    private static final Integer TEST_CHARGE_AMOUNT = 5000;
    private static final Integer TEST_USE_AMOUNT = 3000;
    private static final Integer TEST_BALANCE = 10000;
    private static final Long TEST_TRANSACTION_ID = 1L;
    private static final Integer TEST_TRANSACTION_AMOUNT = 3000;
    private static final Integer TEST_TRANSACTION_BALANCE = 5000;

    @Test
    @DisplayName("포인트 잔액을 조회한다")
    void getBalance() throws Exception {
        // given
        when(userService.getCurrentBalance(TEST_USER_ID)).thenReturn(TEST_BALANCE);

        // when & then
        mockMvc.perform(get("/api/{userId}/balance", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.currentBalance").value(TEST_BALANCE));
    }

    @Test
    @DisplayName("포인트를 충전한다")
    void chargePoint() throws Exception {
        // given
        PointDto.ChargeRequest request = new PointDto.ChargeRequest(TEST_USER_ID, TEST_CHARGE_AMOUNT);
        User userPoint = User.builder()
                .userId(TEST_USER_ID)
                .currentBalance(TEST_CHARGE_AMOUNT)
                .build();

        when(userService.chargePoint(TEST_USER_ID, TEST_CHARGE_AMOUNT)).thenReturn(userPoint);

        // when & then
        mockMvc.perform(post("/api/{userId}/point/charge", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(TEST_CHARGE_AMOUNT));
    }

    @Test
    @DisplayName("포인트를 사용한다")
    void usePoint() throws Exception {
        // given
        PointDto.UseRequest request = new PointDto.UseRequest(TEST_USER_ID, TEST_USE_AMOUNT);
        User userPoint = User.builder()
                .userId(TEST_USER_ID)
                .currentBalance(TEST_TRANSACTION_BALANCE)
                .build();

        when(userService.usePoint(TEST_USER_ID, TEST_USE_AMOUNT)).thenReturn(userPoint);

        // when & then
        mockMvc.perform(post("/api/{userId}/point/use", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(TEST_TRANSACTION_BALANCE));
    }

    @Test
    @DisplayName("포인트 내역을 조회한다")
    void getBalanceHistory() throws Exception {
        // given
        TransactionType transactionType = TransactionType.CHARGE;
        LocalDateTime createdAt = LocalDateTime.now();
        
        List<PointTransaction> transactions = List.of(
            PointTransaction.builder()
                .transactionId(TEST_TRANSACTION_ID)
                .userId(TEST_USER_ID)
                .transactionType(transactionType)
                .amount(TEST_TRANSACTION_AMOUNT)
                .balance(TEST_TRANSACTION_BALANCE)
                .createdAt(createdAt)
                .build()
        );

        List<PointDto.HistoryResponse> expectedResponse = List.of(
            new PointDto.HistoryResponse(TEST_TRANSACTION_ID, TEST_USER_ID, transactionType, TEST_TRANSACTION_AMOUNT, TEST_TRANSACTION_BALANCE, createdAt)
        );

        when(userService.getPointHistory(TEST_USER_ID)).thenReturn(transactions);
        when(userDtoMapper.toHistoryResponseDtoList(transactions)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/users/{userId}/point/history", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(TEST_TRANSACTION_ID))
                .andExpect(jsonPath("$[0].transactionType").value(transactionType.name()))
                .andExpect(jsonPath("$[0].amount").value(TEST_TRANSACTION_AMOUNT));
    }

}