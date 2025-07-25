package kr.hhplus.be.ecommerce.product.presentation;

import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.model.ProductOption;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    ProductDto.ProductResponse toProductResponseDto(Product source);

    List<ProductDto.ProductResponse> toProductResponseDtoList(List<Product> source);

    ProductDto.ProductOptionResponse toProductOptionResponseDto(ProductOption source);
}