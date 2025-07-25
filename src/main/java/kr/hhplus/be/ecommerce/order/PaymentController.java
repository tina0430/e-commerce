package kr.hhplus.be.ecommerce.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/orders/{orderId}/payment")
public class PaymentController {

    // O-3 상품 주문 결제
    @PostMapping
    public ResponseEntity<OrderDto.PaymentResponse> pay(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            @RequestBody OrderDto.PaymentRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new OrderDto.PaymentResponse(null, null, null, null, null, null));
    }
}
