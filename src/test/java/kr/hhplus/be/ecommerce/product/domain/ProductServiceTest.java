package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 도메인 서비스 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPersistenceMapper mapper;

    @InjectMocks
    private ProductService productService;

    private ProductEntity productEntity;
    private Product product;
    private ProductOptionEntity productOptionEntity;
    private ProductOption productOption;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        productEntity = ProductEntity.builder()
                .productId(1L)
                .productName("테스트 상품")
                .createdAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .productId(1L)
                .productName("테스트 상품")
                .createdAt(LocalDateTime.now())
                .build();

        productOptionEntity = ProductOptionEntity.builder()
                .productOptionId(1L)
                .productId(1L)
                .productOptionName("테스트 옵션")
                .quantity(10)
                .price(10000)
                .updatedAt(LocalDateTime.now())
                .build();

        productOption = ProductOption.builder()
                .productOptionId(1L)
                .productId(1L)
                .productOptionName("테스트 옵션")
                .quantity(10)
                .price(10000)
                .updatedAt(LocalDateTime.now())
                .build();

        orderItem = OrderItem.builder()
                .productOptionId(1L)
                .quantity(3)
                .build();
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class ProductQueryTests {

        @Test
        @DisplayName("모든 상품 목록을 조회한다")
        void getAllProducts() {
            // given
            List<ProductEntity> productEntities = List.of(productEntity);
            List<Product> expectedProducts = List.of(product);

            when(productRepository.findAll()).thenReturn(productEntities);
            when(mapper.toProductList(productEntities)).thenReturn(expectedProducts);

            // when
            List<Product> result = productService.getAllProducts();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(1L);
            assertThat(result.get(0).getProductName()).isEqualTo("테스트 상품");

            verify(productRepository).findAll();
            verify(mapper).toProductList(productEntities);
        }

        @Test
        @DisplayName("특정 상품을 조회한다")
        void getProduct() {
            // given
            when(productRepository.findProductById(1L)).thenReturn(Optional.of(productEntity));
            when(mapper.toProduct(productEntity)).thenReturn(product);

            // when
            Product result = productService.getProduct(1L);

            // then
            assertThat(result.getProductId()).isEqualTo(1L);
            assertThat(result.getProductName()).isEqualTo("테스트 상품");

            verify(productRepository).findProductById(1L);
            verify(mapper).toProduct(productEntity);
        }

        @Test
        @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
        void getProduct_NotFound() {
            // given
            when(productRepository.findProductById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProduct(999L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_NOT_FOUND.getCode());
                    });

            verify(productRepository).findProductById(999L);
            verify(mapper, never()).toProduct(any());
        }
    }

    @Nested
    @DisplayName("재고 관리 테스트")
    class StockManagementTests {

        @Test
        @DisplayName("주문 상품들의 재고를 확인하고 차감한다")
        void validateAndReduceStock() {
            // given
            List<OrderItem> items = List.of(orderItem);

            when(productRepository.findProductOptionById(1L)).thenReturn(Optional.of(productOptionEntity));
            when(mapper.toProductOption(productOptionEntity)).thenReturn(productOption);
            doNothing().when(mapper).applyToEntity(any(ProductOption.class), any(ProductOptionEntity.class));

            // when
            productService.validateAndReduceStock(items);

            // then
            verify(productRepository).findProductOptionById(1L);
            verify(mapper).toProductOption(productOptionEntity);
            verify(mapper).applyToEntity(any(ProductOption.class), eq(productOptionEntity));
            assertThat(productOption.getQuantity()).isEqualTo(7); // 10 - 3
        }

        @Test
        @DisplayName("특정 상품 옵션의 재고를 차감한다")
        void decreaseStockIfAvailable() {
            // given
            when(productRepository.findProductOptionById(1L)).thenReturn(Optional.of(productOptionEntity));
            when(mapper.toProductOption(productOptionEntity)).thenReturn(productOption);
            doNothing().when(mapper).applyToEntity(any(ProductOption.class), any(ProductOptionEntity.class));

            // when
            productService.decreaseStockIfAvailable(1L, 3);

            // then
            verify(productRepository).findProductOptionById(1L);
            verify(mapper).toProductOption(productOptionEntity);
            verify(mapper).applyToEntity(any(ProductOption.class), eq(productOptionEntity));
            assertThat(productOption.getQuantity()).isEqualTo(7); // 10 - 3
        }

        @Test
        @DisplayName("존재하지 않는 상품 옵션의 재고를 차감하려 하면 예외가 발생한다")
        void decreaseStockIfAvailable_OptionNotFound() {
            // given
            when(productRepository.findProductOptionById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.decreaseStockIfAvailable(999L, 3))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_OPTION_NOT_FOUND.getCode());
                    });

            verify(productRepository).findProductOptionById(999L);
            verify(mapper, never()).toProductOption(any());
        }

        @Test
        @DisplayName("재고가 부족한 상품 옵션의 재고를 차감하려 하면 예외가 발생한다")
        void decreaseStockIfAvailable_InsufficientStock() {
            // given
            ProductOption lowStockOption = ProductOption.builder()
                    .productOptionId(1L)
                    .quantity(2) // 재고 부족
                    .build();

            when(productRepository.findProductOptionById(1L)).thenReturn(Optional.of(productOptionEntity));
            when(mapper.toProductOption(productOptionEntity)).thenReturn(lowStockOption);

            // when & then
            assertThatThrownBy(() -> productService.decreaseStockIfAvailable(1L, 5))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_OPTION_OUT_OF_STOCK.getCode());
                    });

            verify(productRepository).findProductOptionById(1L);
            verify(mapper).toProductOption(productOptionEntity);
            verify(mapper, never()).applyToEntity(any(), any());
        }

        @Test
        @DisplayName("상품 옵션의 재고를 증가시킨다")
        void increaseStock() {
            // given
            when(productRepository.findProductOptionById(1L)).thenReturn(Optional.of(productOptionEntity));
            when(mapper.toProductOption(productOptionEntity)).thenReturn(productOption);
            doNothing().when(mapper).applyToEntity(any(ProductOption.class), any(ProductOptionEntity.class));

            // when
            productService.increaseStock(1L, 5);

            // then
            verify(productRepository).findProductOptionById(1L);
            verify(mapper).toProductOption(productOptionEntity);
            verify(mapper).applyToEntity(any(ProductOption.class), eq(productOptionEntity));
            assertThat(productOption.getQuantity()).isEqualTo(15); // 10 + 5
        }

        @Test
        @DisplayName("존재하지 않는 상품 옵션의 재고를 증가시키려 하면 예외가 발생한다")
        void increaseStock_OptionNotFound() {
            // given
            when(productRepository.findProductOptionById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.increaseStock(999L, 5))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_OPTION_NOT_FOUND.getCode());
                    });

            verify(productRepository).findProductOptionById(999L);
            verify(mapper, never()).toProductOption(any());
        }
    }

    @Nested
    @DisplayName("상위 판매 상품 테스트")
    class TopSellingProductsTests {

        @Test
        @DisplayName("상위 판매 상품 목록을 조회한다")
        void getTopSellingProducts() {
            // given
            List<ProductEntity> productEntities = List.of(productEntity);
            List<Product> expectedProducts = List.of(product);

            when(productRepository.findAll()).thenReturn(productEntities);
            when(mapper.toProductList(productEntities)).thenReturn(expectedProducts);

            // when
            List<Product> result = productService.getTopSellingProducts();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(1L);

            verify(productRepository).findAll();
            verify(mapper).toProductList(productEntities);
        }
    }

    @Nested
    @DisplayName("에러 처리 테스트")
    class ErrorHandlingTests {

        @Test
        @DisplayName("빈 주문 목록으로 재고 차감을 시도한다")
        void validateAndReduceStock_EmptyItems() {
            // given
            List<OrderItem> emptyItems = List.of();

            // when
            productService.validateAndReduceStock(emptyItems);

            // then
            verify(productRepository, never()).findProductOptionById(anyLong());
            verify(mapper, never()).toProductOption(any());
        }

        @Test
        @DisplayName("여러 주문 상품의 재고를 차감한다")
        void validateAndReduceStock_MultipleItems() {
            // given
            OrderItem item1 = OrderItem.builder().productOptionId(1L).quantity(2).build();
            OrderItem item2 = OrderItem.builder().productOptionId(2L).quantity(3).build();
            List<OrderItem> items = List.of(item1, item2);

            ProductOptionEntity optionEntity2 = ProductOptionEntity.builder()
                    .productOptionId(2L)
                    .quantity(5)
                    .build();

            ProductOption option2 = ProductOption.builder()
                    .productOptionId(2L)
                    .quantity(5)
                    .build();

            when(productRepository.findProductOptionById(1L)).thenReturn(Optional.of(productOptionEntity));
            when(productRepository.findProductOptionById(2L)).thenReturn(Optional.of(optionEntity2));
            when(mapper.toProductOption(productOptionEntity)).thenReturn(productOption);
            when(mapper.toProductOption(optionEntity2)).thenReturn(option2);
            doNothing().when(mapper).applyToEntity(any(ProductOption.class), any(ProductOptionEntity.class));

            // when
            productService.validateAndReduceStock(items);

            // then
            verify(productRepository).findProductOptionById(1L);
            verify(productRepository).findProductOptionById(2L);
            verify(mapper, times(2)).applyToEntity(any(ProductOption.class), any(ProductOptionEntity.class));
            assertThat(productOption.getQuantity()).isEqualTo(8); // 10 - 2
            assertThat(option2.getQuantity()).isEqualTo(2); // 5 - 3
        }
    }
} 