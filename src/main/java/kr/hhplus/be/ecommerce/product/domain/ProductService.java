package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.common.dto.PageRequest;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
import kr.hhplus.be.ecommerce.common.exception.BusinessError;
import kr.hhplus.be.ecommerce.common.exception.BusinessException;
import kr.hhplus.be.ecommerce.order.domain.model.OrderItem;
import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * 상품 목록을 페이징으로 조회합니다.
     * @param pageRequest 페이징 요청 정보
     * @return 페이징된 상품 목록
     */
    public PageResponse<Product> getProductsWithPaging(PageRequest pageRequest) {
        // 커서가 없으면 현재 시간을 기준으로 설정
        LocalDateTime cursor = pageRequest.getCursor() != null ? 
                pageRequest.getCursor() : LocalDateTime.now();
        
        // 페이징된 상품 목록 조회
        List<ProductEntity> productEntities = productRepository
                .findProductsByCreatedAtBeforeOrderByCreatedAtDesc(cursor, pageRequest.getSize());
        
        List<Product> products = mapper.toProductList(productEntities);
        
        // 다음 페이지 존재 여부 확인
        boolean hasNext = false;
        LocalDateTime nextCursor = null;
        
        if (!products.isEmpty()) {
            // 마지막 상품의 생성 시간을 다음 커서로 설정
            nextCursor = products.get(products.size() - 1).getCreatedAt();
            
            // 다음 페이지가 있는지 확인 (마지막 상품보다 이전에 더 많은 상품이 있는지)
            hasNext = productRepository.existsByCreatedAtBefore(nextCursor);
        }
        
        return PageResponse.of(products, nextCursor, hasNext, pageRequest.getSize());
    }

    /**
     * 특정 상품의 상세 정보를 조회합니다.
     * @param productId 상품 ID
     * @return 상품 정보
     */
    public Product getProduct(Long productId) {
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
