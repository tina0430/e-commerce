package kr.hhplus.be.ecommerce.product.presentation;

import kr.hhplus.be.ecommerce.product.domain.model.Product;
import kr.hhplus.be.ecommerce.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ProductDto.ProductResponse>> getProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto.ProductResponse> response = productDtoMapper.toProductResponseDtoList(products);
        return ResponseEntity.ok(response);
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