package kr.hhplus.be.ecommerce.order.domain.model;

import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.order.domain.OrderCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order 도메인 객체
 * 주문 정보를 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long orderId;
    private Long userId;
    private Long userCouponId;
    private Long totalAmount;
    private Long discountAmount;
    private Long finalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void calculateTotalAmount() {
        this.totalAmount = this.orderItems.stream()
                .mapToLong(OrderItem::getFinalPrice)
                .sum();
    }

    /**
     * 주문 생성 팩토리 메서드
     * @param userId 사용자 ID
     * @param userCoupon 쿠폰 (선택사항)
     * @param orderItems 주문 상품 목록
     * @return 생성된 주문
     */
    public static Order createOrder(Long userId, UserCoupon userCoupon, List<OrderItem> orderItems) {
        long totalAmount = orderItems.stream().mapToLong(OrderItem::getPrice).sum();
        OrderCalculator.applyDiscount(userCoupon, orderItems); // todo orderItems를 반환해주는것이 읽기 좋은 코드일까?
        Long userCouponId = userCoupon != null? userCoupon.getUserCouponId() : null;
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .orderItems(new ArrayList<>(orderItems))
                .build();
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalArgumentException("취소할 수 없는 주문 상태입니다.");
        }
        
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 확정
     */
    public void confirm() {
        if (!isPending()) {
            throw new IllegalArgumentException("확정할 수 없는 주문 상태입니다.");
        }
        
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == OrderStatus.CONFIRMED;
    }

    public boolean isShipped() {
        return this.status == OrderStatus.SHIPPING;
    }

    public boolean isDelivered() {
        return this.status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }

    public boolean isRefunded() {
        return this.status == OrderStatus.REFUNDED;
    }

    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }

    public boolean hasCoupon() {
        return this.userCouponId != null;
    }

}
