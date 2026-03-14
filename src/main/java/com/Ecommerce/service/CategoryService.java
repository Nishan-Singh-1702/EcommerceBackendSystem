package com.Ecommerce.service;

import com.Ecommerce.payload.CategoryDTO;
import com.Ecommerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
}
