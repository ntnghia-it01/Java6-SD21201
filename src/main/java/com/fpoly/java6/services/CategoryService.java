package com.fpoly.java6.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java6.beans.CategoryBean;
import com.fpoly.java6.entities.CategoryEntity;
import com.fpoly.java6.jpas.CategoryJPA;

@Service
public class CategoryService {

	@Autowired
	private CategoryJPA categoryJPA;
	
	public void addCategory(CategoryBean bean) {
//		Kiểm tra tên danh mục có tồn tại không?
//		- Nếu có thông báo lỗi
//		- Nếu không lưu vào db
		
		Optional<CategoryEntity> catOptional = categoryJPA.getCategoryByName(bean.getName());
//		Có tồn tại danh mục cùng tên
		if(catOptional.isPresent()) {
			throw new RuntimeException("Danh mục đã tồn tại");
		}
		
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setName(bean.getName());
		
		categoryJPA.save(categoryEntity);
	}
}
