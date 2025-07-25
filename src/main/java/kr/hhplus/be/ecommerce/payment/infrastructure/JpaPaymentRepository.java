package kr.hhplus.be.ecommerce.payment.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hhplus.be.ecommerce.common.infrastructure.JpaRepositoryBase;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import static kr.hhplus.be.ecommerce.payment.domain.model.QPaymentEntity.paymentEntity;

@Repository
public class JpaPaymentRepository extends JpaRepositoryBase {

    public JpaPaymentRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    public Optional<PaymentEntity> findById(Long paymentId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(paymentEntity)
                        .where(paymentEntity.paymentId.eq(paymentId))
                        .fetchOne());
    }

    public Optional<PaymentEntity> findByOrderId(Long orderId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(paymentEntity)
                        .where(paymentEntity.orderId.eq(orderId))
                        .fetchOne());
    }

    public PaymentEntity save(PaymentEntity paymentEntity) {
        return super.save(paymentEntity);
    }
}