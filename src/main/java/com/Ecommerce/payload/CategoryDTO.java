package com.Ecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    public Long categoryId;

    @NotBlank
    @Size(min = 3, message = "Category name must contain atleast 3 characters")
    private String categoryName;
}
