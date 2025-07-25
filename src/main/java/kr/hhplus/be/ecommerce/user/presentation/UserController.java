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
     * @see UserApiSpec#getBalance(UserId)
     */
    @GetMapping("/users/{userId}/point")
    @Override
    public ResponseEntity<PointDto.Response> getBalance(@PathVariable("userId") @Valid UserId userId) {
        Long balance = userService.getBalance(userId.value());
        PointDto.Response response = new PointDto.Response(userId.value(), balance);
        return ResponseEntity.ok(response);
    }

    /**
     * @see UserApiSpec#chargePoint(UserId, PointDto.ChargeRequest)
     */
    @PostMapping("/users/{userId}/point/charge")
    @Override
    public ResponseEntity<PointDto.Response> chargePoint(@PathVariable("userId") @Valid UserId userId, @RequestBody PointDto.ChargeRequest request) {
        User userPoint = userService.chargePoint(userId.value(), request.amount());
        PointDto.Response response = userDtoMapper.toPointResponseDto(userPoint);
        return ResponseEntity.ok(response);
    }

    /**
     * @see UserApiSpec#usePoint(UserId, PointDto.UseRequest)
     */
    @PatchMapping("/users/{userId}/point/use")
    @Override
    public ResponseEntity<PointDto.Response> usePoint(@PathVariable("userId") @Valid UserId userId, @RequestBody PointDto.UseRequest request) {
        User userPoint = userService.usePoint(userId.value(), request.amount());
        PointDto.Response response = userDtoMapper.toPointResponseDto(userPoint);
        return ResponseEntity.ok(response);
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