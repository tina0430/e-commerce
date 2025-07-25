package kr.hhplus.be.ecommerce.order.presentation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.ecommerce.order.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    // O-1 상품 주문
    public record OrderRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            @Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.")
            Long userId,

            Long couponId,

            @NotNull(message = "주문 옵션 리스트는 필수입니다.")
            @NotEmpty(message = "주문 옵션 리스트는 1개 이상이어야 합니다.")
            List<OrderDto.OrderItemRequest> items
    ) {}

    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수입니다.")
            @Min(value = 1, message = "상품 ID는 1 이상이어야 합니다.")
            Long productId,

            @NotNull(message = "상품 옵션 ID는 필수입니다.")
            @Min(value = 1, message = "상품 옵션 ID는 1 이상이어야 합니다.")
            Long productOptionId,

            String productOptionName,

            @NotNull(message = "수량은 필수입니다.")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            Integer quantity,

            @NotNull(message = "금액은 필수입니다.")
            Long price
    ) {}

    public record OrderResponse(Long orderId,
                                Integer totalAmount,
                                OrderStatus status,
                                LocalDateTime createdAt) {}

    // O-2 상품 주문 내역 조회
    public record OrderHistoryResponse(Long orderId,
                                       Integer totalAmount,
                                       OrderStatus status,
                                       List<OrderItemResponse> orderItems,
                                       LocalDateTime createdAt) {}

    public record OrderItemResponse(Long productId,
                                    String productName,
                                    Long productOptionId,
                                    String productOptionName,
                                    Integer quantity,
                                    Integer unitPrice,
                                    Integer discountAmount,
                                    Integer finalPrice) {}


}