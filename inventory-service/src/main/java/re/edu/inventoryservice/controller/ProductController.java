package re.edu.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.edu.inventoryservice.entity.Product;
import re.edu.inventoryservice.service.ProductService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {

        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}/deduct")
    public ResponseEntity<String> deductStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        int result = productService.deductStock(productId, quantity);
        if (result == -1) {
            return ResponseEntity.notFound().build();
        }
        if (result == 0) {
            return ResponseEntity.status(409)
                    .body("Insufficient stock");
        }

        return ResponseEntity.ok("Stock deducted successfully");
    }

    @PutMapping("/{productId}/restore")
    public ResponseEntity<String> restoreStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        productService.restoreStock(productId, quantity);

        return ResponseEntity.ok("Stock restored successfully");
    }
}