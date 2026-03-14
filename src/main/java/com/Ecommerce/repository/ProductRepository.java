package com.Ecommerce.repository;

import com.Ecommerce.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategoryCategoryIdOrderByPriceAsc(Pageable pageDetails, Long categoryId);

    Page<Product> findByProductNameContainingIgnoreCase(Pageable pageDetail, String keyword);

    Product findByProductName(@NotBlank @Size(min = 3, message = "Product name must contain atleast 3 characters") String productName);
}
