package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;

import java.util.Optional;

public interface PaymentRepository {

    PaymentEntity save(PaymentEntity paymentEntity);
    Optional<PaymentEntity> findById(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);

} 