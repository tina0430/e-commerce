package kr.hhplus.be.ecommerce.product.presentation;

import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    @Mapping(target = "options", source = "productOptions")
    ProductDto.ProductResponse toProductResponseDto(Product source);

    List<ProductDto.ProductResponse> toProductResponseDtoList(List<Product> source);

    ProductDto.ProductOptionResponse toProductOptionResponseDto(ProductOption source);

    List<ProductDto.ProductOptionResponse> toProductOptionResponseDtoList(List<ProductOption> source);
}