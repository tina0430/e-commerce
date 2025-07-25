package kr.hhplus.be.ecommerce.order.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.order.application.OrderFacade;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 컨트롤러 테스트")
class OrderControllerTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_COUPON_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_OPTION_ID = 1L;
    private static final String TEST_PRODUCT_OPTION_NAME = "상품 옵션 이름";
    private static final Integer TEST_QUANTITY = 2;
    private static final Long TEST_PRICE = 10000L;
    private static final Long TEST_TOTAL_AMOUNT = 20000L;
    private static final String TEST_USER_ID_STRING = "1";
    private static final String TEST_ORDER_ID_STRING = "1";

    @Mock
    private OrderFacade orderFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderDtoMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("상품 주문")
    class OrderProducts {

        @Test
        @DisplayName("상품 주문 성공")
        void orderProducts_Success() throws Exception {
            // given
            OrderDto.OrderRequest request = createOrderRequest();
            Order order = createOrder();
            OrderDto.OrderResponse response = createOrderResponse();

            when(orderMapper.toOrderItemList(any())).thenReturn(List.of());
            when(orderFacade.orderProducts(eq(TEST_USER_ID), eq(TEST_COUPON_ID), any())).thenReturn(order);
            when(orderMapper.toOrderResponse(order)).thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/users/{userId}/orders", TEST_USER_ID_STRING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$.totalAmount").value(TEST_TOTAL_AMOUNT))
                    .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.name()));
        }
    }

    @Nested
    @DisplayName("주문 내역 조회")
    class GetOrderHistory {

        @Test
        @DisplayName("주문 내역 조회 성공")
        void getOrderHistory_Success() throws Exception {
            // given
            List<Order> orders = List.of(createOrder());
            List<OrderDto.OrderHistoryResponse> responses = List.of(createOrderHistoryResponse());

            when(orderService.getUserOrders(TEST_USER_ID)).thenReturn(orders);
            when(orderMapper.toOrderHistoryResponseList(orders)).thenReturn(responses);

            // when & then
            mockMvc.perform(get("/api/users/{userId}/orders", TEST_USER_ID_STRING))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].orderId").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$[0].totalAmount").value(TEST_TOTAL_AMOUNT))
                    .andExpect(jsonPath("$[0].status").value(OrderStatus.PENDING.name()));
        }
    }

    @Nested
    @DisplayName("주문 상세 조회")
    class GetOrder {

        @Test
        @DisplayName("주문 상세 조회 성공")
        void getOrder_Success() throws Exception {
            // given
            Order order = createOrder();
            OrderDto.OrderHistoryResponse response = createOrderHistoryResponse();

            when(orderService.getOrder(TEST_USER_ID, TEST_ORDER_ID)).thenReturn(order);
            when(orderMapper.toOrderHistoryResponse(order)).thenReturn(response);

            // when & then
            mockMvc.perform(get("/api/users/{userId}/orders/{orderId}", TEST_USER_ID_STRING, TEST_ORDER_ID_STRING))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$.totalAmount").value(TEST_TOTAL_AMOUNT))
                    .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.name()));
        }
    }

    private OrderDto.OrderRequest createOrderRequest() {
        OrderDto.OrderItemRequest itemRequest = new OrderDto.OrderItemRequest(
                TEST_PRODUCT_ID,
                TEST_PRODUCT_OPTION_ID,
                TEST_PRODUCT_OPTION_NAME,
                TEST_QUANTITY,
                TEST_PRICE
        );

        return new OrderDto.OrderRequest(
                TEST_USER_ID,
                TEST_COUPON_ID,
                List.of(itemRequest)
        );
    }

    private Order createOrder() {
        return Order.builder()
                .orderId(TEST_ORDER_ID)
                .userId(TEST_USER_ID)
                .userCouponId(TEST_COUPON_ID)
                .totalAmount(TEST_TOTAL_AMOUNT)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(List.of())
                .build();
    }

    private OrderDto.OrderResponse createOrderResponse() {
        return new OrderDto.OrderResponse(
                TEST_ORDER_ID,
                TEST_TOTAL_AMOUNT.intValue(),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
    }

    private OrderDto.OrderHistoryResponse createOrderHistoryResponse() {
        return new OrderDto.OrderHistoryResponse(
                TEST_ORDER_ID,
                TEST_TOTAL_AMOUNT.intValue(),
                OrderStatus.PENDING,
                List.of(),
                LocalDateTime.now()
        );
    }
} 