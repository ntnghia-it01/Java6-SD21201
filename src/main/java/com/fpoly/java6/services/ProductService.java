package com.fpoly.java6.services;

import org.springframework.stereotype.Service;

import com.fpoly.java6.beans.ProductBean;

@Service
public class ProductService {

	public void addProduct(ProductBean bean) {
//		Kiểm tra danh mục của sản phẩm có tồn tại không?
//		Do client gửi lên catId => Có khả năng danh mục không có trong DB
		
		
//		Dùng base64 để nhận ảnh 
//		base64:
//		- Là một chuỗi 
//		- Định dạng: data:image/<Kiểu ảnh>,<Mã base64>
//		Kiểm tra định dạng:
//		- chuỗi phải bắt đầu bằng data:image/**
//		- Kiểu ảnh: png, jpg, jpge, webp
		
//		- Chuyển base64 => byte[] 
//		- Kiểm tra độ lớn của file => Giới hạn 5MB
//		
//		Lưu ảnh vào folder để có tên ảnh
//		Lưu thông tin sản phẩm vào db
		
		
	}
}
