package com.fpoly.java6.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.java6.beans.CategoryBean;
import com.fpoly.java6.response.CategoryResponse;
import com.fpoly.java6.response.DataResponse;
import com.fpoly.java6.services.CategoryService;
import com.fpoly.java6.services.ErrorServices;

import jakarta.validation.Valid;

@RestController
// Khi client gọi API /category => Controller sẽ kích hoạt
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private ErrorServices errorServices;
	
	@Autowired
	private CategoryService categoryService;

//	Lấy danh sách
	@GetMapping
	public ResponseEntity<DataResponse<List<CategoryResponse>>> getList(){
//		Giả sử gọi đến API này sẽ trả về 1 chuỗi json 
		DataResponse<List<CategoryResponse>> response = new DataResponse<List<CategoryResponse>>();
		response.setSuccess(true);
		response.setMessage("Lấy danh sách danh mục thành công");
		List<CategoryResponse> categoryResponses = categoryService.getCategories();
		response.setData(categoryResponses);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
//	API lấy chi tiết của danh mục 
//	GET /category/1
	@GetMapping("/{id}")
	public ResponseEntity<String> getDetail(@PathVariable(name = "id") int catId){
		
		return ResponseEntity.status(HttpStatus.OK).body("");
	}
	
//	API thêm danh mục
//	POST /category
	@PostMapping
	public ResponseEntity<DataResponse<String>> adCategory(
			@Valid @RequestBody Optional<CategoryBean> bean,
			Errors errors){
		DataResponse<String> response = new DataResponse<String>();
		if (!bean.isPresent()) {
	        Map<String, List<String>> errorMap = new HashMap<>();
	        errorMap.put(
	            "requestBody",
	            Collections.singletonList("Request body không được để trống")
	        );

	        response.setSuccess(false);
	        response.setData(null);
	        response.setErrors(errorMap);

	        return ResponseEntity.badRequest().body(response);
	    }
		
		Map<String, List<String>> errorMap = errorServices.getError(errors);
		
		if(errorMap != null) {
	        response.setSuccess(false);
	        response.setErrors(errorMap);
	        response.setData(null);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		try {
			categoryService.addCategory(bean.get());
		}catch (Exception e) {
			response.setSuccess(false);
			response.setData(null);
			errorMap = new HashMap<String, List<String>>();
			errorMap.put("error", Collections.singletonList(e.getMessage()));
			response.setErrors(errorMap);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		response.setSuccess(true);
		response.setMessage("Tạo danh mục thành công");
		response.setData(null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}

// Làm việc với API. Có 5 phương thức tương ứng
// - GET => Lấy thông tin
//		- Lấy danh sách
//		- Lấy chi tiết
//		- Tìm kiếm
// - POST => Thêm một đối tượng mới
// - PUT => Cập nhật tất cả thông tin của một đối tượng trừ id
// - PATCH => Cập nhật một phần thông tin của đối tượng 
// - DELETE => Xoá một đối tượng 


//	Khi gọi API thì dữ liệu từ API sẽ phản hồi về 2 nội dung quan trọng
//	- status
//		HttpStatus.OK => 200 => Dùng với các API lấy thông tin thành công
//		HttpStatus.CREATED => 201 => Dùng với API khi thêm đối tượng thành công (POST)
//		HttpStatus.ACCEPTED => 202 => Dùng với các API thay đổi thông tin (PUT, PATCH, DELETE)
//		HttpStatus.BAD_REQUEST => 400 => Dùng khi API trả về lỗi dữ liệu đầu vào (validate beans)
//		HttpStatus.UNAUTHORIZED => 401 => Dùng khi API trả về lỗi client không có quyền truy cập
//	- data (Response) => Chuỗi JSON từ API phản hồi về cho client
//	Là một đối tượng khởi tạo ở Java có các thuộc tính
//		- success => true || false => Nếu API lỗi == false, Nếu API thành công == true
//		- message => String => Dùng để thông báo thành công để client hiển thị, 
//			Nếu API lỗi message == null
//		- errors => Map<String, String> => Trả các lỗi khi validate beans hoặc exception
//			Nếu API thành công == null
//		- data => Dữ liệu JSON ở API trả về cho client (JSON Array [], JSON Object {})