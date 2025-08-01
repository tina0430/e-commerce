package kr.hhplus.be.ecommerce.user.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController implements UserApiSpec {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     * @see UserApiSpec#getCurrentBalance(UserId)
     */
    @GetMapping("/{userId}/balance")
    @Override
    public ResponseEntity<PointDto.Response> getCurrentBalance(@PathVariable("userId") @Valid UserId userId) {
        Integer balance = userService.getCurrentBalance(userId.value());
        return ResponseEntity.ok(PointDto.Response.builder()
                .userId(userId.value())
                .currentBalance(balance)
                .build());
    }

    /**
     * 포인트를 충전합니다.
     * @param userId 사용자 ID
     * @param request 포인트 충전 요청
     * @return 충전 후 포인트 정보
     * @see UserApiSpec#chargePoint(UserId, PointDto.ChargeRequest)
     */
    @PostMapping("/{userId}/point/charge")
    @Override
    public ResponseEntity<PointDto.Response> chargePoint(@PathVariable("userId") @Valid UserId userId, @RequestBody PointDto.ChargeRequest request) {
        User user = userService.chargePoint(userId.value(), request.amount());
        return ResponseEntity.ok(PointDto.Response.builder()
                .userId(userId.value())
                .currentBalance(user.getCurrentBalance())
                .build());
    }

    /**
     * 포인트를 사용합니다.
     * @param userId 사용자 ID
     * @param request 포인트 사용 요청
     * @return 사용 후 포인트 정보
     * @see UserApiSpec#usePoint(UserId, PointDto.UseRequest)
     */
    @PostMapping("/{userId}/point/use")
    @Override
    public ResponseEntity<PointDto.Response> usePoint(@PathVariable("userId") @Valid UserId userId, @RequestBody PointDto.UseRequest request) {
        User user = userService.usePoint(userId.value(), request.amount());
        return ResponseEntity.ok(PointDto.Response.builder()
                .userId(userId.value())
                .currentBalance(user.getCurrentBalance())
                .build());
    }

    /**
     * @see UserApiSpec#getPointHistory(UserId)
     */
    @GetMapping("/users/{userId}/point/history")
    @Override
    public ResponseEntity<List<PointDto.HistoryResponse>> getPointHistory(@PathVariable("userId") @Valid UserId userId) {
        List<PointTransaction> pointTransactions = userService.getPointHistory(userId.value());
        List<PointDto.HistoryResponse> response = userDtoMapper.toHistoryResponseDtoList(pointTransactions);
        return ResponseEntity.ok(response);
    }
}