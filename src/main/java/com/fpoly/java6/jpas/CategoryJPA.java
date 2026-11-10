package com.fpoly.java6.jpas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.java6.entities.CategoryEntity;

public interface CategoryJPA extends 
	JpaRepository<CategoryEntity, Integer>{

//	Viết lệnh kiểm tra trùng ở JPA
	@Query(value = "SELECT * FROM categories WHERE LOWER(name) = LOWER(?1)",
			nativeQuery = true)
	public Optional<CategoryEntity> getCategoryByName(String name);
	
	@Query(value = "SELECT * FROM categories WHERE LOWER(name) = LOWER(?1) AND id != ?2",
			nativeQuery = true)
	public Optional<CategoryEntity> getCategoryByNameAndId(String name, int id);
}
