package com.Ecommerce.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @Schema(description = "Category ID",example = "101")
    public Long categoryId;

    @NotBlank
    @Size(min = 3, message = "Category name must contain atleast 3 characters")
    @Schema(description = "Category name you wish to create",example = "Toys")
    private String categoryName;
}
