package com.fpoly.java6.beans;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryBean {
	
	@NotBlank(message = "Tên danh mục không rỗng")
	private String name;
}
