package kr.hhplus.be.ecommerce.payment.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.common.domain.valueObject.OrderId;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import kr.hhplus.be.ecommerce.payment.applicaion.PaymentFacade;
import kr.hhplus.be.ecommerce.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/orders/{orderId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;
    private final PaymentDtoMapper paymentMapper;

    /**
     * @see PaymentApiSpec#payOrder(UserId, OrderId)
     */
    @PostMapping
    public ResponseEntity<PaymentDto.PaymentResponse> payOrder(
            @PathVariable("userId") @Valid UserId userId,
            @PathVariable("orderId") @Valid OrderId orderId) {
        Payment payment = paymentFacade.payOrder(userId.value(), orderId.value());
        PaymentDto.PaymentResponse response = paymentMapper.toPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }
}
