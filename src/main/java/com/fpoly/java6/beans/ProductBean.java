package com.fpoly.java6.beans;

import org.hibernate.validator.constraints.Range;

import com.fpoly.java6.enums.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductBean {
	@NotNull(message = "Tên là bắt buộc")
	@NotBlank(message = "Tên không rỗng")
	private String name;
	
	@NotNull(message = "Mô tả là bắt buộc")
	@NotBlank(message = "Mô tả không rỗng")
	private String desc;
	
	@Min(value = 1000, message = "Giá tối thiểu là 1.000đ")
	private int price;
	
	@Min(value = 0, message = "Số lượng tối thiểu là 0")
	private int quantity;
	
	@Range(min = 1, max = 2, message = "Trạng thái không đúng")
	private ProductStatus status;
//	1 Đang bán
//	2 Ngưng bán
	@NotNull(message = "Hình ảnh là bắt buộc")
	@NotBlank(message = "Hình ảnh là bắt buộc")
	private String image;
	
	@Min(value = 1, message = "Danh mục không đúng")
	private int catId;
}


// Đối tượng class, abstract class, interface

// enum => lưu trữ các hằng số có giá trị hoặc mô tả tương ứng
// Tạo enum để dễ nhớ các giá trị của trạng thái