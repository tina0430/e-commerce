package kr.hhplus.be.ecommerce.coupon.presentation;

import kr.hhplus.be.ecommerce.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.ecommerce.coupon.domain.model.UserCoupon;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponController implements CouponApiSpec {

    private final CouponService couponService;
    private final CouponResponseMapper couponMapper;

    /**
     * @see CouponApiSpec#createAmountCouponPolicy(CouponPolicyDto.CreateAmountCouponRequest)
     */
    @PostMapping("/coupon-policies/amount")
    @Override
    public ResponseEntity<CouponPolicyDto.CreateResponse> createAmountCouponPolicy(@RequestBody CouponPolicyDto.CreateAmountCouponRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new CouponPolicyDto.CreateResponse(1L));
    }

    /**
     * @see CouponApiSpec#createRateCouponPolicy (CouponPolicyInfo.CreateRateCouponRequest)
     */
    @PostMapping("/coupon-policies/rate")
    @Override
    public ResponseEntity<CouponPolicyDto.CreateResponse> createRateCouponPolicy(@RequestBody CouponPolicyDto.CreateRateCouponRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new CouponPolicyDto.CreateResponse(1L));
    }

    /**
     * @see CouponApiSpec#getAvailableCoupons(Long)
     */
    @GetMapping("/users/{userId}/coupons/available")
    @Override
    public ResponseEntity<List<CouponPolicyDto.AvailableCoupon>> getAvailableCoupons(@PathVariable Long userId) {
        List<CouponPolicy> availableCoupons = couponService.getAvailableCoupons(userId);
        List<CouponPolicyDto.AvailableCoupon> response = couponMapper.toAvailableCouponDtoList(availableCoupons);
        return ResponseEntity.ok(response);
    }

    /**
     * @see CouponApiSpec#issueCoupon(UserCouponDto.IssueRequest)
     */
    @PostMapping("/coupons/issue")
    @Override
    public ResponseEntity<UserCouponDto.IssueResponse> issueCoupon(@RequestBody UserCouponDto.IssueRequest request) {
        UserCoupon issuedCoupon = couponService.issueCoupon(request.userId(), request.couponPolicyId());
        UserCouponDto.IssueResponse response = couponMapper.toIssueResponseDto(issuedCoupon);
        return ResponseEntity.ok(response);
    }

    /**
     * @see CouponApiSpec#getUserCoupons(Long)
     */
    @GetMapping("/users/{userId}/coupons")
    @Override
    public ResponseEntity<UserCouponDto.UserCouponListResponse> getUserCoupons(@PathVariable Long userId) {
        List<UserCoupon> issuedCoupons = couponService.getUserCoupons(userId);
        List<UserCouponDto.UserCoupon> userCouponDtos = couponMapper.toUserCouponDtoList(issuedCoupons);
        UserCouponDto.UserCouponListResponse response = new UserCouponDto.UserCouponListResponse(userCouponDtos);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}


