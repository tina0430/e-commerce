package kr.hhplus.be.ecommerce.product.presentation;

import kr.hhplus.be.ecommerce.common.dto.PageRequest;
import kr.hhplus.be.ecommerce.common.dto.PageResponse;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
import kr.hhplus.be.ecommerce.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated // 추후 PathVariable 또는 QueryParam 검증시 필요
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController implements ProductApiSpec {

    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;

    /**
     * @see ProductApiSpec#getProducts()
     */
    @GetMapping("/products")
    @Override
    public ResponseEntity<PageResponse<ProductDto.ProductResponse>> getProducts() {
        PageRequest pageRequest = PageRequest.of(LocalDateTime.now(), 10);
        PageResponse<ProductDto.ProductResponse> response = getProductsWithPaging(pageRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * @see ProductApiSpec#getProductsWithPaging(LocalDateTime, Integer)
     */
    @GetMapping("/products/paging")
    @Override
    public ResponseEntity<PageResponse<ProductDto.ProductResponse>> getProductsWithPaging(
            @RequestParam(value = "cursor", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(cursor, size);
        PageResponse<ProductDto.ProductResponse> response = getProductsWithPaging(pageRequest);
        return ResponseEntity.ok(response);
    }

    private PageResponse<ProductDto.ProductResponse> getProductsWithPaging(PageRequest pageRequest) {
        PageResponse<Product> pagedProducts = productService.getProductsWithPaging(pageRequest);
        List<ProductDto.ProductResponse> productResponses = productDtoMapper.toProductResponseDtoList(pagedProducts.getData());
        PageResponse<ProductDto.ProductResponse> response = PageResponse.of(
                productResponses,
                pagedProducts.getNextCursor(),
                pagedProducts.isHasNext(),
                pagedProducts.getSize()
        );
        return response;
    }

    /**
     * @see ProductApiSpec#getProduct(Long)
     */
    @GetMapping("/products/{productId}")
    @Override
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        ProductDto.ProductResponse response = productDtoMapper.toProductResponseDto(product);
        return ResponseEntity.ok(response);
    }

    /**
     * @see ProductApiSpec#getTopSellingProducts()
     */
    @GetMapping("/products/top-selling")
    @Override
    public ResponseEntity<List<ProductDto.ProductResponse>> getTopSellingProducts() {
        List<Product> products = productService.getTopSellingProducts();
        List<ProductDto.ProductResponse> response = productDtoMapper.toProductResponseDtoList(products);
        return ResponseEntity.ok(response);
    }
}