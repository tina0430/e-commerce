package kr.hhplus.be.ecommerce.order.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
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
    private String productName;
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
        Long price = getPrice();
        if (discountAmount > price) {
            throw new BusinessException(BusinessError.INVALID_DISCOUNT_AMOUNT);
        }
        this.discountAmount = discountAmount;
        this.finalPrice = getPrice() - discountAmount;
    }

    public Long getFinalPrice() {
        return finalPrice != null ? finalPrice : getPrice();
    }

} 