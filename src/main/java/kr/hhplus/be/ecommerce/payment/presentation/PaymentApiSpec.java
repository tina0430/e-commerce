package kr.hhplus.be.ecommerce.payment.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.common.domain.valueObject.OrderId;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "결제", description = "주문 결제 관련 API")
public interface PaymentApiSpec {

    /**
     * O-3 상품 주문 결제
     * 사용자가 주문한 상품에 대해 포인트를 사용하여 결제를 진행합니다.
     * @param userId 사용자 ID
     * @param orderId 주문 ID
     * @return 결제 정보
     */
    @Operation(summary = "상품 주문 결제", description = "사용자가 주문한 상품에 대해 포인트를 사용하여 결제를 진행합니다."
    )
    ResponseEntity<PaymentDto.PaymentResponse> payOrder(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable("userId") @Valid UserId userId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable("orderId") @Valid OrderId orderId);

}