package kr.hhplus.be.ecommerce.user.presentation;

import jakarta.transaction.Transactional;
import kr.hhplus.be.ecommerce.common.domain.valueObject.UserId;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
import kr.hhplus.be.ecommerce.user.domain.UserRepository;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.presentation.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.ecommerce.user.presentation.fixture.UserFixture.BASIC_CURSOR_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("사용자 컨트롤러 통합 테스트")
class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    private List<PointTransactionEntity> transactionEntities;

    @Test
    @DisplayName("커서 없이 포인트 내역을 페이징으로 조회한다")
    @Transactional
    void getPointHistoryWithPaging_WithoutCursor() {
        // given
        transactionEntities = UserFixture.createTransactionEntityList();
        transactionEntities.forEach(userRepository::savePointTransaction);
        UserId userId = UserId.from(transactionEntities.get(0).getUserId());

        // when
        ResponseEntity<PageResponse<PointDto.HistoryResponse>> response = userController.getPointHistory(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(BASIC_CURSOR_SIZE);
    }

    @Test
    @DisplayName("포인트 내역을 커서 기반으로 페이징 조회한다")
    @Transactional
    void getPointHistoryWithPaging() {
        // given
        transactionEntities = UserFixture.createTransactionEntityList();
        transactionEntities.forEach(userRepository::savePointTransaction);
        UserId userId = UserId.from(transactionEntities.get(0).getUserId());
        LocalDateTime cursor = transactionEntities.get(BASIC_CURSOR_SIZE).getCreatedAt(); // BASIC_CURSOR_SIZE + 1번째 데이터
        boolean hasNext = transactionEntities.size() > BASIC_CURSOR_SIZE * 2;

        // when
        ResponseEntity<PageResponse<PointDto.HistoryResponse>> response =
                userController.getPointHistoryWithPaging(userId, cursor, BASIC_CURSOR_SIZE);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isEqualTo(hasNext);
        assertThat(response.getBody().getData()).hasSize(BASIC_CURSOR_SIZE);
    }
}
