package kr.hhplus.be.ecommerce.product.domain.model;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 옵션 도메인 모델 테스트")
class ProductOptionTest {

    private ProductOption productOption;

    @BeforeEach
    void setUp() {
        productOption = ProductOption.builder()
                .productOptionId(1L)
                .productId(1L)
                .productOptionName("테스트 옵션")
                .quantity(10)
                .price(10000)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("재고 확인 테스트")
    class StockValidationTests {

        @Test
        @DisplayName("재고가 있으면 사용 가능하다")
        void isAvailable_WithStock() {
            // given
            ProductOption option = ProductOption.builder()
                    .quantity(5)
                    .build();

            // when & then
            assertThat(option.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("재고가 0이면 사용 불가능하다")
        void isAvailable_NoStock() {
            // given
            ProductOption option = ProductOption.builder()
                    .quantity(0)
                    .build();

            // when & then
            assertThat(option.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("재고가 음수면 사용 불가능하다")
        void isAvailable_NegativeStock() {
            // given
            ProductOption option = ProductOption.builder()
                    .quantity(-1)
                    .build();

            // when & then
            assertThat(option.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("요청 수량이 재고보다 적으면 재고가 있다")
        void hasStock_EnoughStock() {
            // when & then
            assertThat(productOption.hasStock(5)).isTrue();
        }

        @Test
        @DisplayName("요청 수량이 재고와 같으면 재고가 있다")
        void hasStock_ExactStock() {
            // when & then
            assertThat(productOption.hasStock(10)).isTrue();
        }

        @Test
        @DisplayName("요청 수량이 재고보다 많으면 재고가 없다")
        void hasStock_InsufficientStock() {
            // when & then
            assertThat(productOption.hasStock(15)).isFalse();
        }
    }

    @Nested
    @DisplayName("재고 차감 테스트")
    class StockDecreaseTests {

        @Test
        @DisplayName("충분한 재고가 있을 때 재고를 차감한다")
        void decreaseStock_WithEnoughStock() {
            // given
            int originalQuantity = productOption.getQuantity();
            int decreaseAmount = 3;

            // when
            productOption.decreaseStock(decreaseAmount);

            // then
            assertThat(productOption.getQuantity()).isEqualTo(originalQuantity - decreaseAmount);
        }

        @Test
        @DisplayName("재고가 부족할 때 예외가 발생한다")
        void decreaseStock_InsufficientStock() {
            // given
            int decreaseAmount = 15; // 재고(10)보다 많은 수량

            // when & then
            assertThatThrownBy(() -> productOption.decreaseStock(decreaseAmount))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertThat(businessException.getCode()).isEqualTo(BusinessError.PRODUCT_OPTION_OUT_OF_STOCK.getCode());
                    });
        }

        @Test
        @DisplayName("재고를 모두 차감할 수 있다")
        void decreaseStock_AllStock() {
            // given
            int decreaseAmount = 10; // 모든 재고

            // when
            productOption.decreaseStock(decreaseAmount);

            // then
            assertThat(productOption.getQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("0개 차감은 가능하다")
        void decreaseStock_ZeroAmount() {
            // given
            int originalQuantity = productOption.getQuantity();

            // when
            productOption.decreaseStock(0);

            // then
            assertThat(productOption.getQuantity()).isEqualTo(originalQuantity);
        }
    }

    @Nested
    @DisplayName("재고 증가 테스트")
    class StockIncreaseTests {

        @Test
        @DisplayName("재고를 증가시킨다")
        void increaseStock_PositiveAmount() {
            // given
            int originalQuantity = productOption.getQuantity();
            int increaseAmount = 5;

            // when
            productOption.increaseStock(increaseAmount);

            // then
            assertThat(productOption.getQuantity()).isEqualTo(originalQuantity + increaseAmount);
        }

        @Test
        @DisplayName("음수로 재고를 증가시키려 하면 예외가 발생한다")
        void increaseStock_NegativeAmount() {
            // given
            int increaseAmount = -5;

            // when & then
            assertThatThrownBy(() -> productOption.increaseStock(increaseAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("증가할 수량은 0보다 커야 합니다.");
        }

        @Test
        @DisplayName("0으로 재고를 증가시키면 재고가 변하지 않는다")
        void increaseStock_ZeroAmount() {
            // given
            int originalQuantity = productOption.getQuantity();
            int increaseAmount = 0;

            // when
            productOption.increaseStock(increaseAmount);

            // then
            assertThat(productOption.getQuantity()).isEqualTo(originalQuantity);
        }

        @Test
        @DisplayName("재고가 0인 상태에서도 재고를 증가시킬 수 있다")
        void increaseStock_FromZeroStock() {
            // given
            ProductOption zeroStockOption = ProductOption.builder()
                    .quantity(0)
                    .build();
            int increaseAmount = 10;

            // when
            zeroStockOption.increaseStock(increaseAmount);

            // then
            assertThat(zeroStockOption.getQuantity()).isEqualTo(increaseAmount);
        }
    }

    @Nested
    @DisplayName("상품 옵션 생성 테스트")
    class ProductOptionCreationTests {

        @Test
        @DisplayName("상품 옵션을 정상적으로 생성한다")
        void createProductOption() {
            // given & when
            ProductOption option = ProductOption.builder()
                    .productOptionId(1L)
                    .productId(1L)
                    .productOptionName("새로운 옵션")
                    .quantity(20)
                    .price(15000)
                    .updatedAt(LocalDateTime.now())
                    .build();

            // then
            assertThat(option.getProductOptionId()).isEqualTo(1L);
            assertThat(option.getProductId()).isEqualTo(1L);
            assertThat(option.getProductOptionName()).isEqualTo("새로운 옵션");
            assertThat(option.getQuantity()).isEqualTo(20);
            assertThat(option.getPrice()).isEqualTo(15000);
            assertThat(option.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("기본값으로 상품 옵션을 생성한다")
        void createProductOptionWithDefaults() {
            // given & when
            ProductOption option = new ProductOption();

            // then
            assertThat(option.getProductOptionId()).isNull();
            assertThat(option.getProductId()).isNull();
            assertThat(option.getProductOptionName()).isNull();
            assertThat(option.getQuantity()).isNull();
            assertThat(option.getPrice()).isNull();
            assertThat(option.getUpdatedAt()).isNull();
        }
    }
} 