package kr.hhplus.be.ecommerce.order.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.common.domain.valueObject.OrderId;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import kr.hhplus.be.ecommerce.order.application.OrderFacade;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController implements OrderApiSpec {

    private final OrderFacade orderFacade;
    private final OrderService orderService;
    private final OrderDtoMapper orderMapper;

    /**
     * @see OrderApiSpec#orderProducts(UserId, OrderDto.OrderRequest)
     */
    @PostMapping("/users/{userId}/orders")
    @Override
    public ResponseEntity<OrderDto.OrderResponse> orderProducts(@PathVariable("userId") @Valid UserId userId, @RequestBody OrderDto.OrderRequest request) {
        List<OrderItem> orderItems = orderMapper.toOrderItemList(request.items());
        Order order = orderFacade.orderProducts(userId.value(), request.couponId(), orderItems);
        OrderDto.OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * @see OrderApiSpec#getOrderHistory(UserId)
     */
    @GetMapping("/users/{userId}/orders")
    @Override
    public ResponseEntity<List<OrderDto.OrderHistoryResponse>> getOrderHistory(@PathVariable("userId") @Valid UserId userId) {
        List<Order> orders = orderService.getUserOrders(userId.value());
        List<OrderDto.OrderHistoryResponse> responses = orderMapper.toOrderHistoryResponseList(orders);
        return ResponseEntity.ok(responses);
    }

    /**
     * @see OrderApiSpec#getOrder(UserId, OrderId)
     */
    @GetMapping("/users/{userId}/orders/{orderId}")
    @Override
    public ResponseEntity<OrderDto.OrderHistoryResponse> getOrder(
            @PathVariable("userId") @Valid UserId userId,
            @PathVariable("orderId") @Valid OrderId orderId) {
        Order order = orderService.getOrder(userId.value(), orderId.value());
        OrderDto.OrderHistoryResponse response = orderMapper.toOrderHistoryResponse(order);
        return ResponseEntity.ok(response);
    }

}
