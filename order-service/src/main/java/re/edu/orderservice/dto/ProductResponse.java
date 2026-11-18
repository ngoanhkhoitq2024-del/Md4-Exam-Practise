package re.edu.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
