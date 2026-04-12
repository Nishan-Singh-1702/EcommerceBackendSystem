package com.Ecommerce.service;

import com.Ecommerce.payload.CartDTO;
import com.Ecommerce.payload.CartResponse;
import com.Ecommerce.payload.CategoryResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CartDTO getCart();

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long productId);
}
