package kr.hhplus.be.ecommerce.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import kr.hhplus.be.ecommerce.product.ProductDto;

@RestController
@RequestMapping("/api")
public class OrderController implements OrderApiSpec {

    /**
     * @see OrderApiSpec#orderProducts(Long, OrderDto.OrderRequest)
     */
    @PostMapping("/users/{userId}/orders")
    @Override
    public ResponseEntity<OrderDto.OrderResponse> orderProducts(@PathVariable Long userId, @RequestBody OrderDto.OrderRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new OrderDto.OrderResponse(null, null, null, null));
    }

    /**
     * @see OrderApiSpec#getOrderHistory(Long)
     */
    @GetMapping("/users/{userId}/orders")
    @Override
    public ResponseEntity<List<OrderDto.OrderHistoryResponse>> getOrderHistory(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }

    /**
     * @see OrderApiSpec#getOrderDetail(Long, Long)
     */
    @GetMapping("/users/{userId}/orders/{orderId}")
    @Override
    public ResponseEntity<OrderDto.OrderHistoryResponse> getOrderDetail(@PathVariable Long userId, @PathVariable Long orderId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new OrderDto.OrderHistoryResponse(orderId, 0, OrderStatus.PENDING, Collections.emptyList(), null));
    }

    /**
     * @see OrderApiSpec#getOrderedProductsByUserId(Long)
     */
    @GetMapping("/users/{userId}/orders/products")
    @Override
    public ResponseEntity<List<ProductDto.ProductResponse>> getOrderedProductsByUserId(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }
}
