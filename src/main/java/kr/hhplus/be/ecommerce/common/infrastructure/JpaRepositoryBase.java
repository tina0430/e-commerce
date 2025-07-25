package kr.hhplus.be.ecommerce.common.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

/**
 * 모든 JPA Repository의 공통 기능을 제공하는 베이스 클래스
 */
public abstract class JpaRepositoryBase {

    protected final JPAQueryFactory queryFactory;
    protected final EntityManager entityManager;

    public JpaRepositoryBase(JPAQueryFactory queryFactory, EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    /**
     * 엔티티를 저장합니다.
     * @param entity 저장할 엔티티
     * @param <T> 엔티티 타입
     * @return 저장된 엔티티
     */
    protected <T> T save(T entity) {
        if (entity == null) {
            return null;
        }

        try {
            Object id = entityManager.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(entity);
            
            if (id == null) {
                entityManager.persist(entity);
                return entity;
            } else {
                return entityManager.merge(entity);
            }
        } catch (Exception e) {
            // ID 필드를 찾을 수 없는 경우 merge 시도
            return entityManager.merge(entity);
        }
    }
}
