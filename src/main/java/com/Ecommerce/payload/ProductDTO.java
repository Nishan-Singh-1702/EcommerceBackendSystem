package com.Ecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank
    @Size(min = 3, message = "Product name must contain atleast 3 characters")
    private String productName;

    @NonNull
    @Positive
    private Integer quantity;
    @NotBlank
    @Size(min = 3, message = "Product description must contain atleast 3 characters")
    private String description;
    private String image;

    @NonNull
    @Positive
    private Double price;
    private Double discount;
    private Double specialPrice;
}
