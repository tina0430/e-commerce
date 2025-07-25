package kr.hhplus.be.ecommerce.coupon;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController implements CouponApiSpec {

    /**
     * @see CouponApiSpec#createCouponPolicy(UserCouponDto.CreateRequest)
     */
    @PostMapping("/coupon-policies")
    @Override
    public ResponseEntity<UserCouponDto.CreateResponse> createCouponPolicy(@RequestBody UserCouponDto.CreateRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new UserCouponDto.CreateResponse(1L));
    }

    /**
     * @see CouponApiSpec#getAvailableCoupons(Long)
     */
    @GetMapping("/users/{userId}/coupons/available")
    @Override
    public ResponseEntity<List<UserCouponDto.AvailableCoupon>> getAvailableCoupons(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }

    /**
     * @see CouponApiSpec#issueCoupon(UserCouponDto.IssueRequest)
     */
    @PostMapping("/coupons/issue")
    @Override
    public ResponseEntity<UserCouponDto.IssueResponse> issueCoupon(@RequestBody UserCouponDto.IssueRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new UserCouponDto.IssueResponse(1L));
    }

    /**
     * @see CouponApiSpec#getUserCoupons(Long)
     */
    @GetMapping("/users/{userId}/coupons")
    @Override
    public ResponseEntity<UserCouponDto.UserCouponListResponse> getUserCoupons(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new UserCouponDto.UserCouponListResponse(Collections.emptyList()));
    }

    /**
     * @see CouponApiSpec#useCoupon(UserCouponDto.UseRequest)
     */
    @PatchMapping("/coupons/use")
    @Override
    public ResponseEntity<UserCouponDto.UseResponse> useCoupon(@RequestBody UserCouponDto.UseRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new UserCouponDto.UseResponse(request.couponId()));
    }
}