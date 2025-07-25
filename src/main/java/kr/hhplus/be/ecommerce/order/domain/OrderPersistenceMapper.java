package kr.hhplus.be.ecommerce.order.domain;

import kr.hhplus.be.ecommerce.order.domain.model.Order;
import kr.hhplus.be.ecommerce.order.domain.model.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    OrderEntity toOrderEntity(Order source);

    @Mapping(target = "orderItems", ignore = true)
    Order toOrder(OrderEntity source);

    @Mapping(target = "orderItems", ignore = true)
    List<Order> toOrderList(List<OrderEntity> source);

    default void applyToEntity(Order domain, OrderEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setStatus(domain.getStatus());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}
