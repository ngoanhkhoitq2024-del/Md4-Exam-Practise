package re.edu.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.edu.inventoryservice.entity.Product;
import re.edu.inventoryservice.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public int deductStock(Long productId, Integer quantity) {
        if (productId == null || productId <= 0
                || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID or quantity"
            );
        }
        // Kiểm tra sản phẩm có tồn tại không
        if (!productRepository.existsById(productId)) {
            return -1;
        }
        // Thực hiện trừ kho
        return productRepository.deductStock(productId, quantity);
    }

    @Transactional
    public void restoreStock(Long productId, Integer quantity) {

        if (productId == null || productId <= 0
                || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID or quantity"
            );
        }

        productRepository.restoreStock(productId, quantity);
    }
}
