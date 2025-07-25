package kr.hhplus.be.ecommerce.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
public interface ProductApiSpec {

    /**
     * P-2 상품 목록 조회
     * 모든 상품 목록을 조회합니다.
     * @return 상품 목록 응답 DTO
     */
    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    ResponseEntity<List<ProductDto.ProductResponse>> getProducts();

    /**
     * P-1 단일 상품 조회
     * 특정 상품의 상세 정보를 조회합니다.
     * @param productId 상품 ID
     * @return 단일 상품 응답 DTO
     */
    @Operation(summary = "단일 상품 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId);

    /**
     * P-4 상위 판매 상품 조회
     * 판매량이 가장 높은 상위 상품 목록을 조회합니다.
     * @return 상위 판매 상품 목록 응답 DTO
     */
    @Operation(summary = "상위 판매 상품 조회", description = "판매량이 가장 높은 상위 상품 목록을 조회합니다.")
    ResponseEntity<List<ProductDto.ProductResponse>> getTopSellingProducts();
}