package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import kr.hhplus.be.ecommerce.payment.domain.model.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentPersistenceMapper {

    PaymentEntity toPaymentEntity(Payment source);

    Payment toPayment(PaymentEntity source);

} 