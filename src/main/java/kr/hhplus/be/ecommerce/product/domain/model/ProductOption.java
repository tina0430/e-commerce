package kr.hhplus.be.ecommerce.product.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ProductOption 도메인 객체
 * 상품 옵션 정보를 나타내는 순수한 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption {

    private Long productOptionId;
    private Long productId;
    private String productOptionName;
    private Integer quantity;
    private Long price;
    private LocalDateTime createdAt;

    public boolean isAvailable() {
        return this.quantity > 0;
    }

    public boolean hasStock(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new BusinessException(BusinessError.PRODUCT_OPTION_OUT_OF_STOCK);
        }
        this.quantity -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("증가할 수량은 0보다 커야 합니다.");
        }
        this.quantity += quantity;
    }
} 