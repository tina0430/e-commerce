package kr.hhplus.be.ecommerce.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import kr.hhplus.be.ecommerce.product.ProductDto;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderApiSpec {

    /**
     * O-1 상품 주문
     * 사용자가 상품을 주문합니다.
     * @param userId 사용자 ID
     * @param request 주문 요청 DTO
     * @return 주문 응답 DTO
     */
    @Operation(summary = "상품 주문", description = "사용자가 상품을 주문합니다.")
    ResponseEntity<OrderDto.OrderResponse> orderProducts(@PathVariable Long userId, @RequestBody OrderDto.OrderRequest request);

    /**
     * O-2 상품 주문 내역 조회
     * 특정 사용자의 주문 내역을 조회합니다.
     * @param userId 사용자 ID
     * @return 주문 내역 목록 응답 DTO
     */
    @Operation(summary = "상품 주문 내역 조회", description = "특정 사용자의 주문 내역을 조회합니다.")
    ResponseEntity<List<OrderDto.OrderHistoryResponse>> getOrderHistory(@PathVariable Long userId);

    /**
     * O-3 단일 주문 상세 조회
     * 특정 사용자의 단일 주문 상세 정보를 조회합니다.
     * @param userId 사용자 ID
     * @param orderId 주문 ID
     * @return 단일 주문 상세 응답 DTO
     */
    @Operation(summary = "단일 주문 상세 조회", description = "특정 사용자의 단일 주문 상세 정보를 조회합니다.")
    ResponseEntity<OrderDto.OrderHistoryResponse> getOrderDetail(@PathVariable Long userId, @PathVariable Long orderId);

    /**
     * O-4 주문 상품 조회
     * 특정 사용자가 주문한 상품 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 주문 상품 목록 응답 DTO
     */
    @Operation(summary = "주문 상품 조회", description = "특정 사용자가 주문한 상품 목록을 조회합니다.")
    ResponseEntity<List<ProductDto.ProductResponse>> getOrderedProductsByUserId(@PathVariable Long userId);
}