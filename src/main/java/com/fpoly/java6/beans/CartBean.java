package com.fpoly.java6.beans;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartBean {
	@NotNull(message = "Mã sản phẩm không rỗng")
	@Min(value = 1, message = "Mã sản phẩm không đúng")
	private int prodId;
	@NotNull(message = "Số lượng sản phẩm không rỗng")
	@Min(value = 1, message = "Số lượng sản phẩm phải là số dương")
	private int quantity;
}
