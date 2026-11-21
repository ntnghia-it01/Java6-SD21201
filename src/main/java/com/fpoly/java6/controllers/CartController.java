package com.fpoly.java6.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.java6.beans.CartBean;
import com.fpoly.java6.response.DataResponse;
import com.fpoly.java6.services.CartService;
import com.fpoly.java6.services.ErrorServices;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user/cart")
public class CartController {
	
	@Autowired
	private ErrorServices errorServices;
	
	@Autowired
	private CartService cartService;
	
//	Thêm sản phẩm vào giỏ hàng
//	Cần biết thêm sản phẩm vào giỏ của user nào???
//	Cần user phải truyền jwt để xác thực tài khoản và lấy user id
//	Id sản phẩm, số lượng sản phẩm
	@PostMapping
	public ResponseEntity<DataResponse<String>> addToCart(
			@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody Optional<CartBean> beanOptional,
			Errors errors){
		
//		user id
//		Khi controller có tiền tố user được chạy
//		- user id luôn luôn là số => Không cần kiểm tra
//		- user id này luôn tồn tại trong db => Không cần kiểm tra trong db
//		- user id này luôn luôn với trạng thái là không khoá => Không cần kiểm tra status
//		=> Mọi thông tin liên quan đều hợp lệ
		int id = Integer.parseInt(jwt.getSubject());
//		Có thể lấy các thuộc tính cần thiết từ claim
//		String email = jwt.getClaimAsString("email");
		
		DataResponse<String> response = new DataResponse<String>();
		
		if (!beanOptional.isPresent()) {
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
//			Viết services để thêm sản phẩm vào giỏ hàng
			cartService.addToCart(beanOptional.get(), id);
		}catch (Exception e) {
			response.setSuccess(false);
			response.setData(null);
			errorMap = new HashMap<String, List<String>>();
			errorMap.put("error", Collections.singletonList(e.getMessage()));
			response.setErrors(errorMap);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		response.setSuccess(true);
		response.setMessage("Thêm sản phẩm vào giỏ hàng thành công");
		response.setData(null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
}
