package com.Ecommerce.payload;

import com.Ecommerce.model.Order;
import com.Ecommerce.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
}
