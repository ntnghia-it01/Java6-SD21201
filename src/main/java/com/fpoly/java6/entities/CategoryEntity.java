package com.fpoly.java6.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "categories")
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "name", nullable = false, columnDefinition = "NVARCHAR (150)")
	private String name;
	
	@OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY)
	private List<ProductEntity> productEntities;
}

// EAGER (Mặc định) 
// - Khi select dữ liệu của category 
// - Tự động chạy lệnh để lấy danh sách sản phẩm

// LAZY
// - Câu lệnh select danh sách sản phẩm theo danh mục hiện tại sẽ được thực thi
// khi gọi hàm get của danh sách => getProductEntities();