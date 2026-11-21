package com.fpoly.java6.jpas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java6.entities.CartEntity;

public interface CartJPA extends JpaRepository<CartEntity, Integer>{

}
