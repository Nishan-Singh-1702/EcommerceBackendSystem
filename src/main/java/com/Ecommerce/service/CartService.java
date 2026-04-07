package com.Ecommerce.service;

import com.Ecommerce.payload.CartDTO;
import com.Ecommerce.payload.CartResponse;
import com.Ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, int delete);
}
