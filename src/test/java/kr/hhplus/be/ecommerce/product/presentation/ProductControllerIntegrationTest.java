package kr.hhplus.be.ecommerce.product.presentation;

import jakarta.transaction.Transactional;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
import kr.hhplus.be.ecommerce.product.domain.ProductRepository;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.presentation.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static kr.hhplus.be.ecommerce.product.presentation.fixture.ProductFixture.BASIC_CURSOR_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("상품 컨트롤러 통합 테스트")
class ProductControllerIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @Test
    @DisplayName("커서 없이 상품 목록을 페이징으로 조회한다")
    @Transactional
    void getProducts() {
        // given
        List<ProductEntity> productEntities = ProductFixture.createProductEntityList();
        productEntities.forEach(productRepository::save);

        // when
        ResponseEntity<PageResponse<ProductDto.ProductResponse>> response = productController.getProducts();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isEqualTo(BASIC_CURSOR_SIZE);
    }

    @Test
    @DisplayName("상품 목록을 페이징으로 조회한다")
    @Transactional
    void getProductsWithPaging() {
        // given
        List<ProductEntity> productEntities = ProductFixture.createProductEntityList();
        productEntities.forEach(productRepository::save);

        // when
        ResponseEntity<PageResponse<ProductDto.ProductResponse>> response = productController.getProductsWithPaging(null, BASIC_CURSOR_SIZE);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getSize()).isEqualTo(BASIC_CURSOR_SIZE);

    }

}