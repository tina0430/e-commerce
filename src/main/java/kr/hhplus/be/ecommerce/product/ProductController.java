package kr.hhplus.be.ecommerce.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController implements ProductApiSpec {

    /**
     * @see ProductApiSpec#getProducts()
     */
    @GetMapping("/products")
    @Override
    public ResponseEntity<List<ProductDto.ProductResponse>> getProducts() {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }

    /**
     * @see ProductApiSpec#getProduct(Long)
     */
    @GetMapping("/products/{productId}")
    @Override
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId) {
        // TODO: Implement service logic
        return ResponseEntity.ok(new ProductDto.ProductResponse(productId, "Test Product", Collections.emptyList()));
    }

    /**
     * @see ProductApiSpec#getTopSellingProducts()
     */
    @GetMapping("/products/top-selling")
    @Override
    public ResponseEntity<List<ProductDto.ProductResponse>> getTopSellingProducts() {
        // TODO: Implement service logic
        return ResponseEntity.ok(Collections.emptyList());
    }
}