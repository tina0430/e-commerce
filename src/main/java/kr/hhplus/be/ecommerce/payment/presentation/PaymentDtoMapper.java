package kr.hhplus.be.ecommerce.payment.presentation;

import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDtoMapper {

    /**
     * Payment 도메인 객체를 PaymentResponse DTO로 변환
     * @param payment Payment 도메인 객체
     * @return PaymentResponse DTO
     */
    PaymentDto.PaymentResponse toPaymentResponse(Payment payment);

}