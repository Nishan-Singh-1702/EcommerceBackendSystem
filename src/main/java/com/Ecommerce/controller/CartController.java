package com.Ecommerce.controller;

import com.Ecommerce.config.AppConstant;
import com.Ecommerce.payload.CartDTO;
import com.Ecommerce.payload.CartResponse;
import com.Ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/carts/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(productId,quantity));
    }

    @GetMapping("/carts")
    public ResponseEntity<CartResponse> getAllCarts(@RequestParam(name = "pageNumber", defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                    @RequestParam(name = "pageSize", defaultValue = AppConstant.pageSize, required = false)Integer pageSize,
                                                    @RequestParam(name = "sortBy", defaultValue = AppConstant.sortBy, required = false)String sortBy,
                                                    @RequestParam(name = "sortOrder", defaultValue = AppConstant.sortDir, required = false)String sortOrder) {
        return ResponseEntity.ok(cartService.getAllCarts(pageNumber,pageSize,sortBy,sortOrder));
    }




}
