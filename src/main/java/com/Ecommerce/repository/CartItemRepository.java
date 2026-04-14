package com.Ecommerce.repository;

import com.Ecommerce.model.CartItem;
import com.Ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id= ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id= ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

    void deleteAllByProduct(Product productFromDb);

    List<CartItem> findAllByProduct(Product productFromDb);
}
