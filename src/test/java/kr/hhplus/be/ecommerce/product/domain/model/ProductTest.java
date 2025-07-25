package kr.hhplus.be.ecommerce.product.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 도메인 모델 테스트")
class ProductTest {

    private Product product;
    private ProductOption option1;
    private ProductOption option2;

    @BeforeEach
    void setUp() {
        option1 = ProductOption.builder()
                .productOptionId(1L)
                .productId(1L)
                .productOptionName("옵션 1")
                .quantity(10)
                .price(10000L)
                .createdAt(LocalDateTime.now())
                .build();

        option2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(1L)
                .productOptionName("옵션 2")
                .quantity(5)
                .price(15000L)
                .createdAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .productId(1L)
                .productName("테스트 상품")
                .createdAt(LocalDateTime.now())
                .productOptions(List.of(option1, option2))
                .build();
    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class ProductCreationTests {

        @Test
        @DisplayName("상품을 정상적으로 생성한다")
        void createProduct() {
            // given & when
            Product newProduct = Product.builder()
                    .productId(2L)
                    .productName("새로운 상품")
                    .createdAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(newProduct.getProductId()).isEqualTo(2L);
            assertThat(newProduct.getProductName()).isEqualTo("새로운 상품");
            assertThat(newProduct.getCreatedAt()).isNotNull();
            assertThat(newProduct.getProductOptions()).isEmpty();
        }

        @Test
        @DisplayName("기본값으로 상품을 생성한다")
        void createProductWithDefaults() {
            // given & when
            Product emptyProduct = new Product();

            // then
            assertThat(emptyProduct.getProductId()).isNull();
            assertThat(emptyProduct.getProductName()).isNull();
            assertThat(emptyProduct.getCreatedAt()).isNull();
            assertThat(emptyProduct.getProductOptions()).isEmpty();
        }

        @Test
        @DisplayName("상품 옵션과 함께 상품을 생성한다")
        void createProductWithOptions() {
            // given & when
            Product productWithOptions = Product.builder()
                    .productId(3L)
                    .productName("옵션이 있는 상품")
                    .createdAt(LocalDateTime.now())
                    .productOptions(List.of(option1))
                    .build();

            // then
            assertThat(productWithOptions.getProductId()).isEqualTo(3L);
            assertThat(productWithOptions.getProductName()).isEqualTo("옵션이 있는 상품");
            assertThat(productWithOptions.getProductOptions()).hasSize(1);
            assertThat(productWithOptions.getProductOptions().get(0).getProductOptionName()).isEqualTo("옵션 1");
        }
    }

    @Nested
    @DisplayName("상품 옵션 관리 테스트")
    class ProductOptionManagementTests {

        @Test
        @DisplayName("상품의 옵션 목록을 조회한다")
        void getProductOptions() {
            // when & then
            assertThat(product.getProductOptions()).hasSize(2);
            assertThat(product.getProductOptions())
                    .extracting("productOptionName")
                    .containsExactlyInAnyOrder("옵션 1", "옵션 2");
        }

        @Test
        @DisplayName("상품에 옵션이 없으면 빈 리스트를 반환한다")
        void getProductOptions_Empty() {
            // given
            Product productWithoutOptions = Product.builder()
                    .productId(4L)
                    .productName("옵션 없는 상품")
                    .build();

            // when & then
            assertThat(productWithoutOptions.getProductOptions()).isEmpty();
        }

        @Test
        @DisplayName("상품의 특정 옵션을 찾는다")
        void findSpecificOption() {
            // when
            ProductOption foundOption = product.getProductOptions().stream()
                    .filter(option -> option.getProductOptionId().equals(1L))
                    .findFirst()
                    .orElse(null);

            // then
            assertThat(foundOption).isNotNull();
            assertThat(foundOption.getProductOptionName()).isEqualTo("옵션 1");
            assertThat(foundOption.getPrice()).isEqualTo(10000L);
        }

        @Test
        @DisplayName("상품의 옵션 개수를 확인한다")
        void getOptionCount() {
            // when & then
            assertThat(product.getProductOptions()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("상품 정보 테스트")
    class ProductInformationTests {

        @Test
        @DisplayName("상품의 기본 정보를 확인한다")
        void getProductInformation() {
            // when & then
            assertThat(product.getProductId()).isEqualTo(1L);
            assertThat(product.getProductName()).isEqualTo("테스트 상품");
            assertThat(product.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("상품의 최저가 옵션을 찾는다")
        void findLowestPriceOption() {
            // when
            ProductOption lowestPriceOption = product.getProductOptions().stream()
                    .min((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()))
                    .orElse(null);

            // then
            assertThat(lowestPriceOption).isNotNull();
            assertThat(lowestPriceOption.getPrice()).isEqualTo(10000L);
            assertThat(lowestPriceOption.getProductOptionName()).isEqualTo("옵션 1");
        }

        @Test
        @DisplayName("상품의 최고가 옵션을 찾는다")
        void findHighestPriceOption() {
            // when
            ProductOption highestPriceOption = product.getProductOptions().stream()
                    .max((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()))
                    .orElse(null);

            // then
            assertThat(highestPriceOption).isNotNull();
            assertThat(highestPriceOption.getPrice()).isEqualTo(15000L);
            assertThat(highestPriceOption.getProductOptionName()).isEqualTo("옵션 2");
        }

        @Test
        @DisplayName("상품의 가격 범위를 확인한다")
        void getPriceRange() {
            // when
            long minPrice = product.getProductOptions().stream()
                    .mapToLong(ProductOption::getPrice)
                    .min()
                    .orElse(0L);

            long maxPrice = product.getProductOptions().stream()
                    .mapToLong(ProductOption::getPrice)
                    .max()
                    .orElse(0L);

            // then
            assertThat(minPrice).isEqualTo(10000L);
            assertThat(maxPrice).isEqualTo(15000L);
        }
    }

    @Nested
    @DisplayName("상품 옵션 재고 테스트")
    class ProductOptionStockTests {

        @Test
        @DisplayName("상품의 모든 옵션이 재고가 있는지 확인한다")
        void checkAllOptionsAvailability() {
            // when
            boolean allAvailable = product.getProductOptions().stream()
                    .allMatch(ProductOption::isAvailable);

            // then
            assertThat(allAvailable).isTrue();
        }

        @Test
        @DisplayName("상품의 재고가 부족한 옵션이 있는지 확인한다")
        void checkLowStockOptions() {
            // given
            ProductOption lowStockOption = ProductOption.builder()
                    .productOptionId(3L)
                    .productId(1L)
                    .productOptionName("재고 부족 옵션")
                    .quantity(0)
                    .price(20000L)
                    .build();

            Product productWithLowStock = Product.builder()
                    .productId(5L)
                    .productName("재고 부족 상품")
                    .productOptions(List.of(lowStockOption))
                    .build();

            // when
            boolean hasLowStock = productWithLowStock.getProductOptions().stream()
                    .anyMatch(option -> !option.isAvailable());

            // then
            assertThat(hasLowStock).isTrue();
        }

        @Test
        @DisplayName("상품의 총 재고 수량을 계산한다")
        void calculateTotalStock() {
            // when
            int totalStock = product.getProductOptions().stream()
                    .mapToInt(ProductOption::getQuantity)
                    .sum();

            // then
            assertThat(totalStock).isEqualTo(15); // 10 + 5
        }
    }

} 