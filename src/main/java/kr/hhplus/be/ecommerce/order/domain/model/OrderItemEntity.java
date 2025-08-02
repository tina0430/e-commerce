package kr.hhplus.be.ecommerce.order.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.ecommerce.common.domain.EntityBase;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_order_item")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity implements EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_option_id", insertable = false, updatable = false)
    private Long productOptionId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @OneToOne(fetch = FetchType.LAZY) // fixme 똑바로 생각해
    @JoinColumn(name = "product_option_id", nullable = false)
    ProductOptionEntity productOption;

    @Override
    public Object getId() {
        return orderItemId;
    }
}