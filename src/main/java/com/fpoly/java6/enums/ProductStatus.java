package com.fpoly.java6.enums;

public enum ProductStatus {
	ACTIVE(1, "Đang bán"),
	INACTIVE(2, "Ngừng bán");
	
	private final int value;
	private final String desc;
	
	private ProductStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
	
	public ProductStatus getProductStatus(int status) {
//		ProductStatus.values() => Mảng giá trị enum 
		for(ProductStatus productStatus : ProductStatus.values()) {
			if(productStatus.getValue() == status) {
				return productStatus;
			}
		}
		
		return null;
	}
	
}

// Trạng thái sản phẩm
// - Đang bán => 1 => ProductStatus.ACTIVE
// - Nhưng bán => 2 => ProductStatus.INACTIVE