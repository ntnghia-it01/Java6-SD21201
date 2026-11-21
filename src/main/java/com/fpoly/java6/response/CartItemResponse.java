package com.fpoly.java6.response;

import com.fpoly.java6.enums.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemResponse {
	private int id;
	private int quantity;
	private ProductResponse productResponse;
	private CartItemStatus status;
//	enum => 3 trạng thái
//	- Khả dụng avalible
//	- Không đủ hàng unavalible
//	- Ngừng bán unactive
	
//	Trả về phía FE để FE hiển thị kèm theo sản phẩm ở giỏ hàng
//	Và 2 TH phía dưới ở FE sẽ không có đặt hàng
	
	public static enum CartItemStatus {
		AVALIBLE(1, "Có thể đặt hàng"),
		UNAVALIBLE(2, "Số lượng không khả dụng"),
		INACTIVE(3, "Sản phẩm ngừng bán");
		
		private final int value;
		private final String desc;
		
		private CartItemStatus(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
		
		public static CartItemStatus getProductStatus(int status) {
//			ProductStatus.values() => Mảng giá trị enum 
			for(CartItemStatus cartItemStatus : CartItemStatus.values()) {
				if(cartItemStatus.getValue() == status) {
					return cartItemStatus;
				}
			}
			
			return null;
		}
	}
}