package kr.hhplus.be.ecommerce.order.presentation;

import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "unitPrice", source = "price")
    @Mapping(target = "discountAmount", constant = "0L") // 이 시점엔 모름
    @Mapping(target = "finalPrice", ignore = true) // 이 시점엔 모름
    OrderItem toOrderItem(OrderDto.OrderItemRequest item);

    List<OrderItem> toOrderItemList(List<OrderDto.OrderItemRequest> items);

    OrderDto.OrderResponse toOrderResponse(Order order);

    OrderDto.OrderHistoryResponse toOrderHistoryResponse(Order order);

    List<OrderDto.OrderHistoryResponse> toOrderHistoryResponseList(List<Order> order);

    OrderDto.OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderDto.OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);
}