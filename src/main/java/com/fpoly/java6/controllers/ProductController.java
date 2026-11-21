package com.fpoly.java6.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.java6.beans.ProductBean;
import com.fpoly.java6.response.DataResponse;
import com.fpoly.java6.response.ProductResponse;
import com.fpoly.java6.services.ErrorServices;
import com.fpoly.java6.services.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {
	
	@Autowired
	private ErrorServices errorServices;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/admin/products")
	public ResponseEntity<DataResponse<List<ProductResponse>>> productList(){
		DataResponse<List<ProductResponse>> response = new DataResponse<List<ProductResponse>>();
		
		response.setSuccess(true);
		response.setMessage("Lấy danh sách sản phẩm thành công");
		response.setData(productService.getProducts());
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/products")
	public ResponseEntity<DataResponse<List<ProductResponse>>> productListPublic(){
		DataResponse<List<ProductResponse>> response = new DataResponse<List<ProductResponse>>();
		
		response.setSuccess(true);
		response.setMessage("Lấy danh sách sản phẩm thành công");
		response.setData(productService.getProductsPublic());
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<DataResponse<ProductResponse>> productDetail(
			@PathVariable(name = "id") String idString){
		DataResponse<ProductResponse> response = new DataResponse<ProductResponse>();
		
		try {
			int id = Integer.parseInt(idString);
			
			response.setData(productService.getProductDetail(id));
			response.setSuccess(true);
	        response.setMessage("Lấy chi tiết sản phẩm thành công");
	        
	        return ResponseEntity.status(HttpStatus.OK).body(response);
		}catch(Exception e) {
			Map<String, List<String>> errorMap = new HashMap<>();
	        errorMap.put(
	            "error",
	            Collections.singletonList("Mã sản phẩm không đúng định dạng")
	        );

	        response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

//	url /products POST
//	Thêm sản phẩm vào db
	@PostMapping("/admin/products")
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
		
		try {
			productService.addProduct(beanOptional.get());
		}catch (Exception e) {
			errorMap = new HashMap<>();
	        errorMap.put("error", Collections.singletonList(e.getMessage()));
	        response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		response.setSuccess(true);
		response.setMessage("Tạo danh mục thành công");
		response.setData(null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PatchMapping("/admin/products/{id}")
	public ResponseEntity<DataResponse<String>> updateStatus(
			@PathVariable(name = "id") String idString,
			@RequestParam(name = "status") String statusString){
//		Đang có 2 biến kiểu String
//		Cần kiểm tra 2 biến này có phải là số hay không?
//		Và cần thông báo rõ ràng ở mỗi TH
		DataResponse<String> response = new DataResponse<String>();
		
		int id = -1;
		int status = -1;
		
		try {
			id = Integer.parseInt(idString);
			status = Integer.parseInt(statusString);
			if(status != 1 && status != 2) {
				status = -1;
				throw new RuntimeException();
			}
			
			productService.updateProductStatus(id, status);
			
			response.setSuccess(true);
			response.setMessage("Cập nhật trạng thái thành công");
			response.setData(null);
			
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		}catch(Exception e) {
			
			Map<String, List<String>> errorMap = new HashMap<>();
			
			if(id == -1) {
		        errorMap.put("id",Collections.singletonList("Id sản phẩm không đúng"));
			}else if (status == -1){
				errorMap.put("status",Collections.singletonList("Trạng thái sản phẩm không đúng"));
			}else {
				errorMap.put("error",Collections.singletonList(e.getMessage()));
			}
			
			response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//			Ở đây làm sao biết được id hay status lỗi để thông báo 
		}
	}
	
	@PutMapping("/admin/products/{id}")
	public ResponseEntity<DataResponse<String>> updateProduct(
			@PathVariable(name = "id") String idString,
			@Valid @RequestBody Optional<ProductBean> beanOptional,
			Errors errors){
		DataResponse<String> response = new DataResponse<String>();
		try {
			int id = Integer.parseInt(idString);
			
//			Không có body
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
			
//			Validate bean
			Map<String, List<String>> errorMap = errorServices.getError(errors);
			if(errorMap != null) {
		        response.setSuccess(false);
		        response.setErrors(errorMap);
		        response.setData(null);
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			
//			Thực hiện cập nhật thông tin sản phẩm
			
			productService.updateProduct(beanOptional.get(), id);
			
			response.setSuccess(true);
			response.setMessage("Cập nhật sản phẩm thành công");
			response.setData(null);
			
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
			
		}catch (NumberFormatException e) {
			Map<String, List<String>> errorMap = new HashMap<>();
			
			errorMap.put("id",Collections.singletonList("Id sản phẩm không đúng"));
			
			response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}catch (Exception e) {
			Map<String, List<String>> errorMap = new HashMap<>();
			
			errorMap.put("error",Collections.singletonList("Có lỗi khi sửa sản phẩm"));
			
			response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}
