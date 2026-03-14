package com.Ecommerce.controller;

import com.Ecommerce.config.AppConstant;
import com.Ecommerce.payload.CategoryDTO;
import com.Ecommerce.payload.CategoryResponse;
import com.Ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategory(@RequestParam(name = "pageNumber", defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                           @RequestParam(name = "pageSize",defaultValue = AppConstant.pageSize,required = false)Integer pageSize,
                                                           @RequestParam(name = "sortBy",defaultValue = AppConstant.sortBy,required = false) String sortBy,
                                                           @RequestParam(name = "sortOrder",defaultValue = AppConstant.sortDir,required = false)String sortOrder){
        return ResponseEntity.ok(categoryService.getAllCategory(pageNumber,pageSize,sortBy,sortOrder));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDTO));
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId,categoryDTO));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

}
