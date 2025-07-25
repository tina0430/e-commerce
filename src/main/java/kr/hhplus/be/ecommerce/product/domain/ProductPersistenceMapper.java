package kr.hhplus.be.ecommerce.product.domain;

import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import kr.hhplus.be.ecommerce.product.domain.model.ProductEntity;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    @Mapping(target = "productOptions", ignore = true)
    Product toProduct(ProductEntity source);

    List<Product> toProductList(List<ProductEntity> source);

    ProductOption toProductOption(ProductOptionEntity source);

    /**
     * 도메인 객체의 변경된 상태를 기존 엔티티에 적용합니다.
     * <p>
     * 이 메서드는 새로운 엔티티를 생성하지 않으며,
     * 전달받은 엔티티 객체의 상태만 변경합니다.
     *
     * @param domain 도메인 객체
     * @param entity 기존 엔티티 (상태가 변경됨)
     */
    default void applyToEntity(ProductOption domain, ProductOptionEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setQuantity(domain.getQuantity());
    }

}
