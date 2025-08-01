package kr.hhplus.be.ecommerce.user.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import kr.hhplus.be.ecommerce.common.dto.PageRequest;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    public ResponseEntity<PageResponse<PointDto.HistoryResponse>> getPointHistory(@PathVariable("userId") @Valid UserId userId) {
        PageRequest pageRequest = PageRequest.of(LocalDateTime.now(), 10);
        PageResponse<PointDto.HistoryResponse> response = getPointHistoryWithPaging(userId, pageRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 거래 내역을 페이징으로 조회합니다.
     * @param userId 사용자 ID
     * @param cursor 커서 (날짜 기준, ISO 8601 형식)
     * @param size 페이지 크기 (기본값: 10) // todo 설정으로 뺄것
     * @return 페이징된 포인트 거래 내역
     */
    @GetMapping("/users/{userId}/point/history/paging")
    @Override
    public ResponseEntity<PageResponse<PointDto.HistoryResponse>> getPointHistoryWithPaging(
            @PathVariable("userId") @Valid UserId userId,
            @RequestParam(value = "cursor", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(value = "size", defaultValue = "10") Integer size) { // todo 설정값으로 빼기
        PageRequest pageRequest = PageRequest.of(cursor, size);
        PageResponse<PointDto.HistoryResponse> response = getPointHistoryWithPaging(userId, pageRequest);
        return ResponseEntity.ok(response);
    }

    private PageResponse<PointDto.HistoryResponse> getPointHistoryWithPaging(UserId userId, PageRequest pageRequest) {
        PageResponse<PointTransaction> pagedTransactions = userService.getPointHistoryWithPaging(userId.value(), pageRequest);
        List<PointDto.HistoryResponse> historyResponses = userDtoMapper.toHistoryResponseDtoList(pagedTransactions.getData());
        PageResponse<PointDto.HistoryResponse> response = PageResponse.of(
                historyResponses,
                pagedTransactions.getNextCursor(),
                pagedTransactions.isHasNext(),
                pagedTransactions.getSize()
        );
        return response;
    }
}