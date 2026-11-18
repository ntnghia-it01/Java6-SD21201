package com.fpoly.java6.jpas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.java6.entities.ProductEntity;

public interface ProductJPA extends JpaRepository<ProductEntity, Integer>{
	@Query(value = "SELECT * FROM products WHERE status = 1", nativeQuery = true)
	public List<ProductEntity> getListByStatusTrue();
}
