package com.Ecommerce.service;

import com.Ecommerce.payload.ProductDTO;
import com.Ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO createProduct(Long categoryId, ProductDTO productDTO);
    ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse getProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId);
    ProductResponse getProductByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO deleteProduct(Long productId);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

}
