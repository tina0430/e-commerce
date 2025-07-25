package kr.hhplus.be.ecommerce.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OrderItem 도메인 객체
 * 주문 상품 항목을 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Long productOptionId;
    private String productOptionName;
    private Integer quantity;
    private Long unitPrice;
    private Long discountAmount;
    private Long finalPrice;

    public Long getPrice() {
        return unitPrice * quantity;
    }

    public void applyDiscount(Long discountAmount) {
        this.discountAmount = discountAmount;
        this.finalPrice = getPrice() - discountAmount;
    }

    public Long getFinalPrice() {
        return finalPrice != null ? finalPrice : getPrice();
    }

} 