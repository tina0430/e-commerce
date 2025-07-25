package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 도메인 서비스
 * 상품과 관련된 비즈니스 로직을 담당
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPersistenceMapper mapper;

    /**
     * 모든 상품 목록을 조회합니다.
     * @return 상품 목록
     */
    public List<Product> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return mapper.toProductList(productEntities);
    }

    /**
     * 특정 상품의 상세 정보를 조회합니다.
     * @param productId 상품 ID
     * @return 상품 정보
     */
    public Product getProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(BusinessError.INVALID_PRODUCT_ID);
        }
        ProductEntity productEntity = productRepository.findProductById(productId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
        return mapper.toProduct(productEntity);
    }

    /**
     * 주문 상품들의 재고를 확인하고 차감합니다.
     * @param items 주문 상품 목록
     */
    public void validateAndReduceStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            decreaseStockIfAvailable(item.getProductOptionId(), item.getQuantity());
        }
    }

    /**
     * 특정 상품 옵션의 재고를 차감합니다.
     * @param productOptionId 상품 옵션 ID
     * @param requestedQuantity 요청 수량
     */
    public void decreaseStockIfAvailable(Long productOptionId, Integer requestedQuantity) {
        ProductOptionEntity productOptionEntity = productRepository.findProductOptionById(productOptionId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_OPTION_NOT_FOUND));
        ProductOption productOption = mapper.toProductOption(productOptionEntity);
        productOption.decreaseStock(requestedQuantity);
        mapper.applyToEntity(productOption, productOptionEntity);
    }

    /**
     * 상위 판매 상품 목록을 조회합니다.
     * @return 상위 판매 상품 목록
     */
    public List<Product> getTopSellingProducts() {
        // TODO: 실제 판매량 기반 로직 구현
        // 현재는 모든 상품을 반환
        return getAllProducts();
    }

    /**
     * 상품 옵션의 재고를 증가시킵니다. // todo 롤백때는 이거 안쓰지 않나?
     * @param productOptionId 상품 옵션 ID
     * @param quantity 증가할 수량
     */
    public void increaseStock(Long productOptionId, Integer quantity) {
        ProductOptionEntity productOptionEntity = productRepository.findProductOptionById(productOptionId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_OPTION_NOT_FOUND));

        ProductOption productOption = mapper.toProductOption(productOptionEntity);
        productOption.increaseStock(quantity);

        mapper.applyToEntity(productOption, productOptionEntity);
    }
}
