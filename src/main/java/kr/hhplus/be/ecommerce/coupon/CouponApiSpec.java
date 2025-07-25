package kr.hhplus.be.ecommerce.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "쿠폰", description = "쿠폰 관련 API")
public interface CouponApiSpec {

    /**
     * C-1 쿠폰 정책 발행
     * 관리자가 새로운 쿠폰 정책을 발행합니다.
     * @param request 쿠폰 정책 생성 요청 DTO
     * @return 생성된 쿠폰 정책 응답 DTO
     */
    @Operation(summary = "쿠폰 정책 발행", description = "관리자가 새로운 쿠폰 정책을 발행합니다.")
    ResponseEntity<UserCouponDto.CreateResponse> createCouponPolicy(UserCouponDto.CreateRequest request);

    /**
     * C-2 발급 가능 쿠폰 조회
     * 특정 사용자가 자신이 발급받을 수 있는 쿠폰 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 발급 가능한 쿠폰 목록 응답 DTO
     */
    @Operation(summary = "발급 가능 쿠폰 조회", description = "특정 사용자가 자신이 발급받을 수 있는 쿠폰 목록을 조회합니다.")
    ResponseEntity<List<UserCouponDto.AvailableCoupon>> getAvailableCoupons(@PathVariable Long userId);

    /**
     * C-3 사용자 쿠폰 발급
     * 사용자에게 특정 쿠폰을 발급합니다.
     * @param request 쿠폰 발급 요청 DTO
     * @return 발급된 사용자 쿠폰 응답 DTO
     */
    @Operation(summary = "사용자 쿠폰 발급", description = "사용자에게 특정 쿠폰을 발급합니다.")
    ResponseEntity<UserCouponDto.IssueResponse> issueCoupon(UserCouponDto.IssueRequest request);

    /**
     * C-4 사용자 쿠폰 목록 조회
     * 특정 사용자가 보유한 쿠폰 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 쿠폰 목록 응답 DTO
     */
    @Operation(summary = "사용자 쿠폰 목록 조회", description = "특정 사용자가 보유한 쿠폰 목록을 조회합니다.")
    ResponseEntity<UserCouponDto.UserCouponListResponse> getUserCoupons(Long userId);

    /**
     * C-5 사용자 쿠폰 사용
     * 사용자가 보유한 쿠폰을 사용합니다.
     * @param request 쿠폰 사용 요청 DTO
     * @return 사용된 사용자 쿠폰 응답 DTO
     */
    @Operation(summary = "사용자 쿠폰 사용", description = "사용자가 보유한 쿠폰을 사용합니다.")
    ResponseEntity<UserCouponDto.UseResponse> useCoupon(UserCouponDto.UseRequest request);
}