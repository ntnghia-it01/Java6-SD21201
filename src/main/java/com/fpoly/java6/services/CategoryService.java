package com.fpoly.java6.services;

import java.sql.SQLException;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.fpoly.java6.beans.CategoryBean;
import com.fpoly.java6.entities.CategoryEntity;
import com.fpoly.java6.jpas.CategoryJPA;
import com.fpoly.java6.response.CategoryResponse;

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
	
	public List<CategoryResponse> getCategories(){
		List<CategoryEntity> categoryEntities = categoryJPA.findAll();
		
		List<CategoryResponse> categoryResponses = new ArrayList<CategoryResponse>();
		
		for(CategoryEntity categoryEntity : categoryEntities) {
			categoryResponses.add(convertEntityToResponse(categoryEntity));
		}
		
		return categoryResponses;
	}
	
	public CategoryResponse getCategoryDetail(String id) {
		try {
			int catId = Integer.parseInt(id);
			
			Optional<CategoryEntity> catOptional = categoryJPA.findById(catId);
			
//			Không tìm thấy danh mục
			if(!catOptional.isPresent()) {
				throw new RuntimeException();
			}
			
			return convertEntityToResponse(catOptional.get());
		}catch (Exception e) {
//			Không nên thông báo ID phải là số 
			throw new RuntimeException("Danh mục không tồn tại");
		}
	}
	
//	Danh mục không tồn tại với id
//	Trùng tên danh mục
	public void updateCategory(String id, CategoryBean bean) {
//		Kiểm tra parse id và danh mục có tồn tại ở DB không? 
		int catId = 0;
		try {
			catId = Integer.parseInt(id);
			
			Optional<CategoryEntity> catOptional = categoryJPA.findById(catId);
			
			if(!catOptional.isPresent()) {
				throw new RuntimeException();
			}
		}catch (Exception e) {
			throw new RuntimeException("Danh mục không tồn tại");
		}
		
//		Kiểm tra trùng tên
		Optional<CategoryEntity> catOptional = categoryJPA
				.getCategoryByNameAndId(bean.getName(), catId);
		
		if(catOptional.isPresent()) {
			throw new RuntimeException("Tên danh mục đã tồn tại");
		}
		
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setId(catId);
		categoryEntity.setName(bean.getName());
		categoryJPA.save(categoryEntity);
	}
	
	public void deleteCategory(String id) {
		try {
			int catId = Integer.parseInt(id);
			
			Optional<CategoryEntity> catOptional = categoryJPA.findById(catId);
			
			if(!catOptional.isPresent()) {
				throw new RuntimeException();
			}
			
			categoryJPA.deleteById(catId);
		}catch (NumberFormatException numberFormatException) {
//			Lỗi parse int
			throw new RuntimeException("Danh mục không tồn tại");
		}catch (RuntimeException runtimeException) {
//			Lỗi danh mục không tồn tại
			throw new RuntimeException("Danh mục không tồn tại");
		}catch (Exception exception) {
//			Lỗi khi xoá danh mục
			throw new RuntimeException("Không thể xoá danh mục");
		}
	}
	
	private CategoryResponse convertEntityToResponse(CategoryEntity categoryEntity) {
		CategoryResponse categoryResponse = new CategoryResponse();
		categoryResponse.setId(categoryEntity.getId());
		categoryResponse.setName(categoryEntity.getName());
		return categoryResponse;
	}
}
