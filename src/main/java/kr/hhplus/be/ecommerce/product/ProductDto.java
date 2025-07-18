package kr.hhplus.be.ecommerce.product;

import java.util.List;

public class ProductDto {

    // P-1 상품 조회
    // P-2 상품 목록 조회
    public record ProductResponse(Long productId,
                                  String name,
                                  List<ProductOptionResponse> options) {

    }

    public record ProductOptionResponse(Long productOptionId,
                                        Long productId,
                                        String name,
                                        Integer quantity,
                                        Integer price) {

    }

    // P-4 상위 상품 조회
    public record TopProductResponse(Long productId,
                                     String name,
                                     List<ProductOptionResponse> options) {

    }
}
