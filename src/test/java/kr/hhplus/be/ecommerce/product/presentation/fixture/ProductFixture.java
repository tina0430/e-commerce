package kr.hhplus.be.ecommerce.product.presentation.fixture;

import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductFixture {
    
    public static final Integer BASIC_CURSOR_SIZE = 10;
    public static final Integer TEST_PRODUCT_LIST_SIZE = 100;
    
    public static ProductEntity createProductEntityWithName(String productName) {
        return ProductEntity.builder()
                .productName(productName)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static List<ProductEntity> createProductEntityList() {
        List<ProductEntity> products = new ArrayList<>();
        for (int i = 0; i < TEST_PRODUCT_LIST_SIZE; i++) {
            products.add(createProductEntityWithName("테스트 상품 " + i));
        }
        return products.stream()
                .sorted(Comparator.comparing(ProductEntity::getCreatedAt).reversed())
                .toList();
    }

    public static ProductOptionEntity createProductOptionEntityWithName(Long productId, String productOptionName) {
        LocalDateTime updatedAt = LocalDateTime.now();
        return ProductOptionEntity.builder()
                .productId(productId)
                .productOptionName(productOptionName)
                .updatedAt(updatedAt)
                .build();
    }
    
    public static List<ProductOptionEntity> createProductOptionEntityList(Long productId) {
        List<ProductOptionEntity> productOptions = new ArrayList<>();
        for (int i = 0; i < TEST_PRODUCT_LIST_SIZE; i++) {
            productOptions.add(createProductOptionEntityWithName(productId, "테스트 상품 옵션" + i));
        }
        return productOptions.stream()
                .sorted(Comparator.comparing(ProductOptionEntity::getUpdatedAt).reversed())
                .toList();
    }

}
