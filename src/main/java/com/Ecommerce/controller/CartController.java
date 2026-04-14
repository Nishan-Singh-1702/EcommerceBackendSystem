package com.Ecommerce.controller;

import com.Ecommerce.config.AppConstant;
import com.Ecommerce.exception.APIException;
import com.Ecommerce.model.Cart;
import com.Ecommerce.payload.CartDTO;
import com.Ecommerce.payload.CartResponse;
import com.Ecommerce.repository.CartRepository;
import com.Ecommerce.service.CartService;
import com.Ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartService cartService;

    @PostMapping("/carts/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(productId,quantity));
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<CartResponse> getAllCarts(@RequestParam(name = "pageNumber", defaultValue = AppConstant.pageNumber,required = false)Integer pageNumber,
                                                    @RequestParam(name = "pageSize", defaultValue = AppConstant.pageSize, required = false)Integer pageSize,
                                                    @RequestParam(name = "sortBy", defaultValue = AppConstant.sortBy, required = false)String sortBy,
                                                    @RequestParam(name = "sortOrder", defaultValue = AppConstant.sortDir, required = false)String sortOrder) {
        return ResponseEntity.ok(cartService.getAllCarts(pageNumber,pageSize,sortBy,sortOrder));
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        return ResponseEntity.ok(cartService.getCart());
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation){
        if (!operation.equalsIgnoreCase("delete") && !operation.equalsIgnoreCase("add")) {
            throw new APIException("Invalid operation. Use 'add' or 'delete'");
        }
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/carts/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long productId){
        return ResponseEntity.ok(cartService.deleteProductFromCart(productId));
    }

}
