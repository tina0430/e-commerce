package kr.hhplus.be.ecommerce.payment.infrastructure;

import kr.hhplus.be.ecommerce.payment.domain.PaymentRepository;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImp implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public PaymentEntity save(PaymentEntity paymentEntity) {
        return jpaPaymentRepository.save(paymentEntity);
    }

    @Override
    public Optional<PaymentEntity> findById(Long paymentId) {
        return jpaPaymentRepository.findById(paymentId);
    }

    @Override
    public Optional<PaymentEntity> findByOrderId(Long orderId) {
        return jpaPaymentRepository.findByOrderId(orderId);
    }

} 