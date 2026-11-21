package com.fpoly.java6.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java6.beans.CartBean;
import com.fpoly.java6.entities.CartEntity;
import com.fpoly.java6.entities.CartItemEntity;
import com.fpoly.java6.entities.ProductEntity;
import com.fpoly.java6.entities.UserEntity;
import com.fpoly.java6.enums.ProductStatus;
import com.fpoly.java6.jpas.CartItemJPA;
import com.fpoly.java6.jpas.CartJPA;
import com.fpoly.java6.jpas.ProductJPA;
import com.fpoly.java6.jpas.UserJPA;

@Service
public class CartService {

	@Autowired
	private UserJPA userJPA;
	
	@Autowired
	private CartJPA cartJPA;
	
	@Autowired
	private CartItemJPA cartItemJPA;
	
	@Autowired
	private ProductJPA productJPA;
	
	public void addToCart(CartBean bean, int userId) {
//		User có giỏ hàng chưa? -> Nếu chưa tạo giỏ hàng
		UserEntity userEntity = userJPA.findById(userId).get();
		
		CartEntity cartEntity = userEntity.getCartEntity();
		if(cartEntity == null) {
			cartEntity = new CartEntity();
			cartEntity.setUserEntity(userEntity);
			
			cartEntity = cartJPA.save(cartEntity);
		}
//		Lấy cart ở db => cart không có => Khởi tạo entity mới
//		=> cartJPA.save(cartEntity) lưu xong sẽ trả về entity 
//		=> cartEntity = cartJPA.save(cartEntity) gán giá trị mới cho cartEntity
		
//		Sản phẩm có tồn tại không?
		Optional<ProductEntity> prodOptional = productJPA.findById(bean.getProdId());
		if(!prodOptional.isPresent()) {
			throw new RuntimeException("Sản phẩm không tồn tại");
		}
		ProductEntity productEntity = prodOptional.get();
//		Sản phẩm còn bán không?
		if(ProductStatus.getProductStatus(productEntity.getStatus()).equals(ProductStatus.INACTIVE)) {
			throw new RuntimeException("Sản phẩm đã ngừng kinh doanh");
		}
//		Số lượng sản phẩm có khả dụng không?
//		- TH 1: Sản phẩm chưa có trong giỏ hàng
//		- TH 2: Sản phẩm đã có trong giỏ hàng
//			- Lấy số lượng trong giỏ  + Số lượng muốn thêm <= Số lượng sản phẩm => Thêm

		Optional<CartItemEntity> cartItemOptional = cartItemJPA
				.getCartItemByCartIdAndProductId(cartEntity.getId(), productEntity.getId());
		
		if(!cartItemOptional.isPresent()) {
			if(bean.getQuantity() > productEntity.getQuantity()) {
				throw new RuntimeException("Số lượng sản phẩm không khả dụng");
			}
			
			CartItemEntity cartItemEntity = new CartItemEntity();
			cartItemEntity.setCartEntity(cartEntity);
			cartItemEntity.setProductEntity(productEntity);
			cartItemEntity.setQuantity(bean.getQuantity());
//			Thêm mới
			cartItemJPA.save(cartItemEntity);
		}else {
			CartItemEntity cartItemEntity = cartItemOptional.get();
			
			if(bean.getQuantity() + cartItemEntity.getQuantity() > productEntity.getQuantity()) {
				throw new RuntimeException("Số lượng sản phẩm không khả dụng");
			}
			
			cartItemEntity.setQuantity(bean.getQuantity() + cartItemEntity.getQuantity());
//			Cập nhật số lượng
			cartItemJPA.save(cartItemEntity);
		}
	}
}
