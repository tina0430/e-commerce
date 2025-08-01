package kr.hhplus.be.ecommerce.product.infrastructure;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductRepository {

    private final EntityManager entityManager;

    public JpaProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<ProductEntity> findProductById(Long productId) {
        return Optional.ofNullable(entityManager.find(ProductEntity.class, productId));
    }

    public List<ProductEntity> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM ProductEntity p", ProductEntity.class)
                .getResultList();
    }

    public ProductEntity save(ProductEntity productEntity) {
        if (productEntity.getProductId() == null) {
            entityManager.persist(productEntity);
            return productEntity;
        } else {
            return entityManager.merge(productEntity);
        }
    }
    
    /**
     * 상품 페이징 조회 (날짜 기준)
     */
    public List<ProductEntity> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime cursor, int size) {
        return entityManager.createQuery(
                "SELECT p FROM ProductEntity p " +
                "WHERE p.createdAt < :cursor " +
                "ORDER BY p.createdAt DESC", ProductEntity.class)
                .setParameter("cursor", cursor)
                .setMaxResults(size)
                .getResultList();
    }
    
    /**
     * 다음 페이지 존재 여부 확인
     */
    public boolean existsByCreatedAtBefore(LocalDateTime cursor) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(p) FROM ProductEntity p " +
                "WHERE p.createdAt < :cursor", Long.class)
                .setParameter("cursor", cursor)
                .getSingleResult();
        return count > 0;
    }

    /**
     * 상품 옵션
     */
    public Optional<ProductOptionEntity> findProductOptionById(Long productOptionId) {
        return Optional.ofNullable(entityManager.find(ProductOptionEntity.class, productOptionId));
    }

    public List<ProductOptionEntity> findProductOptionByProductId(Long productId) {
        return entityManager.createQuery(
                "SELECT po FROM ProductOptionEntity po " +
                "WHERE po.productId = :productId", ProductOptionEntity.class)
                .setParameter("productId", productId)
                .getResultList();
    }

} 