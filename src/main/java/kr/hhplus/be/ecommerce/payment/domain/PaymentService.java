package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentPersistenceMapper paymentMapper;

    /**
     * 결제를 생성합니다.
     * @param orderId 주문 ID
     * @param originalPrice 원가
     * @param discountAmount 할인 금액
     * @return 생성된 결제
     */
    @Transactional
    public Payment createPayment(Long orderId, Long originalPrice, Long discountAmount, Long finalPrice) {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .orderId(orderId)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return paymentMapper.toPayment(paymentRepository.save(paymentEntity));
    }

    /**
     * 결제를 성공 처리합니다.
     * @param paymentId 결제 ID
     * @return 성공 처리된 결제
     */
    public Payment processPaymentSuccess(Long paymentId) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(BusinessError.PAYMENT_NOT_FOUND));
        
        Payment payment = paymentMapper.toPayment(paymentEntity);
        payment.updateStatus(PaymentStatus.SUCCESS);
        
        PaymentEntity updatedPaymentEntity = paymentMapper.toPaymentEntity(payment);
        PaymentEntity savedPaymentEntity = paymentRepository.save(updatedPaymentEntity);
        
        return paymentMapper.toPayment(savedPaymentEntity);
    }

    /**
     * 주문 ID로 결제를 조회합니다.
     * @param orderId 주문 ID
     * @return 결제 정보
     */
    public Payment getPaymentByOrderId(Long orderId) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(BusinessError.PAYMENT_NOT_FOUND));
        
        return paymentMapper.toPayment(paymentEntity);
    }

}
