package re.edu.orderservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import re.edu.orderservice.client.InventoryClient;
import re.edu.orderservice.dto.OrderRequest;
import re.edu.orderservice.dto.ProductResponse;
import re.edu.orderservice.entity.Order;
import re.edu.orderservice.entity.OrderStatus;
import re.edu.orderservice.exception.InsufficientStockException;
import re.edu.orderservice.exception.ProductNotFoundException;
import re.edu.orderservice.repository.OrderRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public Order createOrder(OrderRequest request) {
        // 1. Lấy thông tin sản phẩm từ Inventory Service
        ProductResponse product;

        try {
            product = inventoryClient.getProduct(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("Product not found");
        }

        // 2. Kiểm tra tồn kho
        if (product.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        // 3. Trừ tồn kho trong Inventory Service
        try {
            inventoryClient.deductStock(
                    request.getProductId(),
                    request.getQuantity()
            );
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("Product not found");
        } catch (FeignException.Conflict e) {
            throw new InsufficientStockException("Insufficient stock");
        }

        // 4. Tính tổng tiền
        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 5. Tạo đơn hàng
        Order order = new Order();
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(totalPrice);

        // 6. Lưu đơn hàng
        try {
            return orderRepository.save(order);
        } catch (Exception e) {

            // Hoàn lại tồn kho nếu lưu đơn hàng thất bại
            inventoryClient.restoreStock(
                    request.getProductId(),
                    request.getQuantity()
            );
            throw e;
        }
    }
}