package com.Ecommerce.controller;

import com.Ecommerce.config.AppConstant;
import com.Ecommerce.payload.ProductDTO;
import com.Ecommerce.payload.ProductResponse;
import com.Ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(categoryId,productDTO));
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(@RequestParam(name = "pageNumber",defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                         @RequestParam(name = "pageSize",defaultValue = AppConstant.pageSize,required = false)Integer pageSize,
                                                         @RequestParam(name = "sortBy",defaultValue = AppConstant.sortBy,required = false)String sortBy,
                                                         @RequestParam(name = "sortOrder",defaultValue = AppConstant.sortDir,required = false)String sortOrder){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProduct(pageNumber,pageSize,sortBy,sortOrder));
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@RequestParam(name = "pageNumber",defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                                @RequestParam(name = "pageSize",defaultValue = AppConstant.pageSize,required = false)Integer pageSize,
                                                                @RequestParam(name = "sortBy",defaultValue = AppConstant.sortBy,required = false)String sortBy,
                                                                @RequestParam(name = "sortOrder",defaultValue = AppConstant.sortDir,required = false)String sortOrder,
                                                                @PathVariable Long categoryId){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductsByCategory(pageNumber,pageSize,sortBy,sortOrder,categoryId));
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@RequestParam(name = "pageNumber",defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                               @RequestParam(name = "pageSize",defaultValue = AppConstant.pageSize,required = false)Integer pageSize,
                                                               @RequestParam(name = "sortBy",defaultValue = AppConstant.sortBy,required = false)String sortBy,
                                                               @RequestParam(name = "sortOrder",defaultValue = AppConstant.sortDir,required = false)String sortOrder,
                                                               @PathVariable String keyword){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductByKeyword(pageNumber,pageSize,sortBy,sortOrder,keyword));
    }

    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productId,productDTO));
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productId));
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam(name = "image")MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.updateProductImage(productId,image));
    }
}
