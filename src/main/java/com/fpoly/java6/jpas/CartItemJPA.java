package com.fpoly.java6.jpas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.java6.entities.CartItemEntity;

public interface CartItemJPA extends JpaRepository<CartItemEntity, Integer>{
//	Viết select để kiểm tra sản phẩm có thêm vào giỏ hàng của user hiện tại chưa???
	
	@Query(value = "SELECT * FROM cart_items WHERE cart_id=?1 AND product_id=?2", nativeQuery = true)
	public Optional<CartItemEntity> getCartItemByCartIdAndProductId(int cartId, int prodId);
}
