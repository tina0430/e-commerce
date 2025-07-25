package kr.hhplus.be.ecommerce.product.presentation;

import java.util.List;

public class ProductDto {

    // P-1 상품 조회
    // P-2 상품 목록 조회
    public record ProductResponse(Long productId,
                                  String productName,
                                  List<ProductOptionResponse> options) {

    }

    public record ProductOptionResponse(Long productOptionId,
                                        Long productId,
                                        String productOptionName,
                                        Integer quantity,
                                        Long price) {

    }

    // P-4 상위 상품 조회
    public record TopProductResponse(Long productId,
                                     String productName,
                                     List<ProductOptionResponse> options) {

    }
}
