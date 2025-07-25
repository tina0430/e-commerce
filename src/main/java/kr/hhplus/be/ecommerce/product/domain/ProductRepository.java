package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    /**
     * 상품
     */
    Optional<ProductEntity> findProductById(Long productId);
    List<ProductEntity> findAll();

    /**
     * 상품 옵션
     */
    Optional<ProductOptionEntity> findProductOptionById(Long productOptionId);
    List<ProductOptionEntity> findProductOptionByProductId(Long productId);
} 