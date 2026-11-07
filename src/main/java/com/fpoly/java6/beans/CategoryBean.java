package com.fpoly.java6.beans;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryBean {
	
	@NotBlank(message = "Tên danh mục không rỗng")
	@Length(min = 5, message = "Tên danh mục bắt buộc ít nhất 5 ký tự")
	@NotNull(message = "Tên danh mục là bắt buộc")
	private String name;
}