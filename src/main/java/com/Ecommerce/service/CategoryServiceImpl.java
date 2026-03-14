package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.exception.ResourceNotFoundException;
import com.Ecommerce.model.Category;
import com.Ecommerce.payload.CategoryDTO;
import com.Ecommerce.payload.CategoryResponse;
import com.Ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty()) throw new APIException("No Category Created till now !!");
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category,CategoryDTO.class))
                .toList();

        CategoryResponse response = new CategoryResponse();
        response.setContent(categoryDTOS);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElement(categoryPage.getTotalElements());
        response.setTotalPage(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());
        return response;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category categoryFromDb = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(categoryFromDb!=null) throw new APIException("Category already exist with categoryName: "+categoryDTO.getCategoryName());
        Category category = modelMapper.map(categoryDTO,Category.class);
        return modelMapper.map(categoryRepository.save(category),CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category categoryFromDb = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(categoryFromDb!=null && !categoryFromDb.getCategoryId().equals(categoryId)) throw new APIException("Category already exist with categoryName: "+categoryDTO.getCategoryName());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        category.setCategoryName(categoryDTO.getCategoryName());
        return modelMapper.map(categoryRepository.save(category),CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category categoryFromDb = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(categoryFromDb);
        return modelMapper.map(categoryFromDb,CategoryDTO.class);
    }
}
