package re.edu.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import re.edu.orderservice.dto.ProductResponse;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    ProductResponse getProduct(@PathVariable("productId") Long productId);

    @PutMapping("/api/inventory/{productId}/deduct")
    void deductStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );

    @PutMapping("/api/inventory/{productId}/restore")
    void restoreStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );
}
