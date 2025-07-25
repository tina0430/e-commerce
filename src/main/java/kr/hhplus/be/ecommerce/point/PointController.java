package kr.hhplus.be.ecommerce.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PointController implements PointApiSpec {

    /**
     * @see PointApiSpec#getPoint(Long)
     */
    @GetMapping("/users/{userId}/point")
    @Override
    public ResponseEntity<PointDto.Response> getPoint(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new PointDto.Response(userId, 1000L));
    }

    /**
     * @see PointApiSpec#chargePoint(Long, PointDto.ChargeRequest)
     */
    @PostMapping("/users/{userId}/point/charge")
    @Override
    public ResponseEntity<PointDto.Response> chargePoint(@PathVariable Long userId, @RequestBody PointDto.ChargeRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new PointDto.Response(userId, 1000L + request.amount()));
    }

    /**
     * @see PointApiSpec#usePoint(Long, PointDto.UseRequest)
     */
    @PatchMapping("/users/{userId}/point/use")
    @Override
    public ResponseEntity<PointDto.Response> usePoint(@PathVariable Long userId, @RequestBody PointDto.UseRequest request) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new PointDto.Response(userId, 1000L - request.amount()));
    }

    /**
     * @see PointApiSpec#getPointHistory(Long)
     */
    @GetMapping("/users/{userId}/point/history")
    @Override
    public ResponseEntity<List<PointDto.HistoryResponse>> getPointHistory(@PathVariable Long userId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }
}