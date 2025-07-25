package kr.hhplus.be.ecommerce.product.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hhplus.be.ecommerce.common.infrastructure.JpaRepositoryBase;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.product.domain.model.QProductEntity.productEntity;
import static kr.hhplus.be.ecommerce.product.domain.model.QProductOptionEntity.productOptionEntity;

@Repository
public class JpaProductRepository extends JpaRepositoryBase {

    public JpaProductRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    public Optional<ProductEntity> findProductById(Long productId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(productEntity)
                        .where(productEntity.productId.eq(productId))
                        .fetchOne());
    }

    public List<ProductEntity> findAll() {
        return queryFactory.selectFrom(productEntity).fetch();
    }

    /**
     * 상품 옵션
     */
    public Optional<ProductOptionEntity> findProductOptionById(Long productOptionId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(productOptionEntity)
                        .where(productOptionEntity.productOptionId.eq(productOptionId))
                        .fetchOne());
    }

    public List<ProductOptionEntity> findProductOptionByProductId(Long productId) {
        return queryFactory
                .selectFrom(productOptionEntity)
                .where(productOptionEntity.productId.eq(productId))
                .fetch();
    }

} 