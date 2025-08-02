package kr.hhplus.be.ecommerce.coupon.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_coupon_policy")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CouponPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_policy_id")
    private Long couponPolicyId;

    @Column(name = "coupon_name", nullable = false, length = 200)
    private String couponName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;

    @Column(name = "max_discount_amount")
    private Integer maxDiscountAmount;

    @Column(name = "min_order_amount")
    private Integer minOrderAmount;

    @Column(name = "issue_start_at")
    private LocalDateTime issueStartAt;

    @Column(name = "issue_end_at")
    private LocalDateTime issueEndAt;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    @Column(name = "valid_duration_days")
    private Integer validDurationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status", nullable = false)
    private CouponPolicyStatus couponStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
