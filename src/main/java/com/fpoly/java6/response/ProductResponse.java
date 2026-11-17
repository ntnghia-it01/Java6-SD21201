package com.fpoly.java6.response;

import com.fpoly.java6.entities.CategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse {
	private int id;
	private String name;
	private String desc;
	private int price;
	private int quantity = 0;
	private int status = 1;
	private String image;
	private CategoryResponse category;
	private String statusString;
}
