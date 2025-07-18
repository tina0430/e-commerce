package kr.hhplus.be.ecommerce.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "포인트", description = "포인트 관련 API")
public interface PointApiSpec {

    /**
     * B-1 잔액 조회
     * 특정 사용자의 현재 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 응답 DTO
     */
    @Operation(summary = "잔액 조회", description = "특정 사용자의 현재 포인트 잔액을 조회합니다.")
    ResponseEntity<PointDto.Response> getPoint(@PathVariable Long userId);

    /**
     * B-2 잔액 충전
     * 특정 사용자의 포인트 잔액을 충전합니다.
     * @param userId 사용자 ID
     * @param request 포인트 충전 요청 DTO
     * @return 충전 후 포인트 잔액 응답 DTO
     */
    @Operation(summary = "잔액 충전", description = "특정 사용자의 포인트 잔액을 충전합니다.")
    ResponseEntity<PointDto.Response> chargePoint(@PathVariable Long userId, @RequestBody PointDto.ChargeRequest request);

    /**
     * B-3 포인트 사용
     * 특정 사용자의 포인트를 사용합니다.
     * @param userId 사용자 ID
     * @param request 포인트 사용 요청 DTO
     * @return 사용 후 포인트 잔액 응답 DTO
     */
    @Operation(summary = "포인트 사용", description = "특정 사용자의 포인트를 사용합니다.")
    ResponseEntity<PointDto.Response> usePoint(@PathVariable Long userId, @RequestBody PointDto.UseRequest request);

    /**
     * B-4 포인트 내역 조회
     * 특정 사용자의 포인트 충전 및 사용 내역을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 내역 목록 응답 DTO
     */
    @Operation(summary = "포인트 내역 조회", description = "특정 사용자의 포인트 충전 및 사용 내역을 조회합니다.")
    ResponseEntity<List<PointDto.HistoryResponse>> getPointHistory(@PathVariable Long userId);
}