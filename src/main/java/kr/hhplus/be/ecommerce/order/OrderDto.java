package kr.hhplus.be.ecommerce.order;

import kr.hhplus.be.ecommerce.product.ProductDto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    // O-1 상품 주문
    public record OrderRequest(Long userId, List<ProductDto.ProductResponse> items) {}

    public record OrderResponse(Long orderId,
                                Integer totalAmount,
                                OrderStatus status,
                                LocalDateTime createdAt) {}

    // O-2 상품 주문 내역 조회
    public record OrderHistoryResponse(Long orderId,
                                       Integer totalAmount,
                                       OrderStatus status,
                                       List<OrderItemResponse> items,
                                       LocalDateTime createdAt) {}

    public record OrderItemResponse(Long productId,
                                    String productName,
                                    Long productOptionId,
                                    String productOptionName,
                                    Integer quantity,
                                    Integer unitPrice,
                                    Integer discountAmount,
                                    Integer finalPrice) {}

    // O-3 상품 주문 결제
    public record PaymentRequest(Long couponId) {}

    public record PaymentResponse(Long paymentId,
                                  Integer originalPrice,
                                  Integer discountAmount,
                                  Integer finalPrice,
                                  PaymentStatus status,
                                  LocalDateTime createdAt) {

    }
}