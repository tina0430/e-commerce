package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    /**
     * 상품
     */
    Optional<ProductEntity> findProductById(Long productId);
    List<ProductEntity> findAll();
    ProductEntity save(ProductEntity productEntity);
    
    /**
     * 상품 페이징 조회 (날짜 기준)
     */
    List<ProductEntity> findProductsByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime cursor, int size);
    
    /**
     * 다음 페이지 존재 여부 확인
     */
    boolean existsByCreatedAtBefore(LocalDateTime cursor);

    /**
     * 상품 옵션
     */
    Optional<ProductOptionEntity> findProductOptionById(Long productOptionId);
    List<ProductOptionEntity> findProductOptionByProductId(Long productId);
} 