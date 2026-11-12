package com.fpoly.java6.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.java6.beans.ProductBean;
import com.fpoly.java6.response.DataResponse;
import com.fpoly.java6.services.ErrorServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ErrorServices errorServices;

//	url /products POST
//	Thêm sản phẩm vào db
	@PostMapping
	public ResponseEntity<DataResponse<String>> addProduct(
			@Valid @RequestBody Optional<ProductBean> beanOptional,
			Errors errors){
		
		DataResponse<String> response = new DataResponse<String>();
		
//		Validate body null 
		if (!beanOptional.isPresent()) {
	        Map<String, List<String>> errorMap = new HashMap<>();
	        errorMap.put(
	            "requestBody",
	            Collections.singletonList("Request body không được để trống")
	        );

	        response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
		
//		Validate bean
		Map<String, List<String>> errorMap = errorServices.getError(errors);
		if(errorMap != null) {
	        response.setSuccess(false);
	        response.setErrors(errorMap);
	        response.setData(null);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
//		Gọi service để lưu vào db
		
		response.setSuccess(true);
		response.setMessage("Tạo danh mục thành công");
		response.setData(null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
