package com.fpoly.java6.jpas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java6.entities.ProductEntity;

public interface ProductJPA extends JpaRepository<ProductEntity, Integer>{

}
