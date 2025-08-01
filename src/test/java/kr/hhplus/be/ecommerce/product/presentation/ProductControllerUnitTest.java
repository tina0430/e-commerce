package kr.hhplus.be.ecommerce.product.presentation;

import kr.hhplus.be.ecommerce.product.domain.ProductService;
import kr.hhplus.be.ecommerce.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("상품 컨트롤러 단위 테스트")
class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductDtoMapper productDtoMapper;

    @Test
    @DisplayName("상품 목록을 조회한다")
    void getProducts() throws Exception {
        Long productId1 = 1L;
        Long productId2 = 2L;
        String productName1 = "테스트 상품 1";
        String productName2 = "테스트 상품 2";
        // given
        List<Product> products = List.of(
            Product.builder()
                .productId(productId1)
                .productName(productName1)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .productId(productId2)
                .productName(productName2)
                .createdAt(LocalDateTime.now())
                .build()
        );

        List<ProductDto.ProductResponse> expectedResponse = List.of(
            new ProductDto.ProductResponse(productId1, productName1, List.of()),
            new ProductDto.ProductResponse(productId2, productName2, List.of())
        );

        when(productService.getAllProducts()).thenReturn(products);
        when(productDtoMapper.toProductResponseDtoList(products)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId1))
                .andExpect(jsonPath("$[0].productName").value(productName1))
                .andExpect(jsonPath("$[1].productId").value(productId2))
                .andExpect(jsonPath("$[1].productName").value(productName2));
    }

    @Test
    @DisplayName("특정 상품의 상세 정보를 조회한다")
    void getProduct() throws Exception {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        Product product = Product.builder()
                .productId(productId)
                .productName(productName)
                .createdAt(LocalDateTime.now())
                .build();

        ProductDto.ProductResponse expectedResponse = new ProductDto.ProductResponse(productId, productName, List.of());

        when(productService.getProduct(productId)).thenReturn(product);
        when(productDtoMapper.toProductResponseDto(product)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.productName").value(productName));
    }

    @Test
    @DisplayName("상위 판매 상품 목록을 조회한다")
    void getTopSellingProducts() throws Exception {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        String productName1 = "인기 상품 1";
        String productName2 = "인기 상품 2";
        List<Product> topProducts = List.of(
            Product.builder()
                .productId(productId1)
                .productName(productName1)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .productId(productId1)
                .productName(productName2)
                .createdAt(LocalDateTime.now())
                .build()
        );

        List<ProductDto.ProductResponse> expectedResponse = List.of(
            new ProductDto.ProductResponse(productId1, productName1, List.of()),
            new ProductDto.ProductResponse(productId2, productName2, List.of())
        );

        when(productService.getTopSellingProducts()).thenReturn(topProducts);
        when(productDtoMapper.toProductResponseDtoList(topProducts)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/products/top-selling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId1))
                .andExpect(jsonPath("$[0].productName").value(productName1))
                .andExpect(jsonPath("$[1].productId").value(productId2))
                .andExpect(jsonPath("$[1].productName").value(productName2));
    }
} 