package kr.hhplus.be.ecommerce.product.infrastructure;

import kr.hhplus.be.ecommerce.product.domain.ProductRepository;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImp implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    /**
     * 상품
     */
    @Override
    public Optional<ProductEntity> findProductById(Long productId) {
        return jpaProductRepository.findProductById(productId);
    }

    @Override
    public List<ProductEntity> findAll() {
        return jpaProductRepository.findAll();
    }

    /**
     * 상품 옵션
     */
    @Override
    public Optional<ProductOptionEntity> findProductOptionById(Long productOptionId) {
        return jpaProductRepository.findProductOptionById(productOptionId);
    }

    @Override
    public List<ProductOptionEntity> findProductOptionByProductId(Long productId) {
        return jpaProductRepository.findProductOptionByProductId(productId);
    }
} 