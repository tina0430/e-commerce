package kr.hhplus.be.ecommerce.order.domain;

import kr.hhplus.be.ecommerce.coupon.domain.model.DiscountType;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;

import java.util.List;

/**
 * 주문 총액 계산, 쿠폰 할인 분배 등 복잡한 도메인 계산 로직을 수행하는 도메인 서비스입니다.
 * Order와 책임 분리(SRP)를 통해 계산 로직의 재사용성과 테스트 용이성을 높였습니다.
 */
public class OrderCalculator {

    public static void applyDiscount(UserCoupon coupon, List<OrderItem> orderItems) {
        if (coupon == null) return;
        int totalAmount = orderItems.stream().mapToInt(OrderItem::getPrice).sum();
        int totalDiscount = coupon.getDiscountType() == DiscountType.AMOUNT
                ? coupon.getDiscountValue()
                : Math.round(totalAmount * coupon.getDiscountValue() / 100f);
        int remaining = totalDiscount;
        for (OrderItem orderItem : orderItems) {
            int discount = totalDiscount / orderItems.size();
            if (remaining < discount) discount = remaining;
            orderItem.applyDiscount(discount);
            remaining -= discount;
        }
    }

}
