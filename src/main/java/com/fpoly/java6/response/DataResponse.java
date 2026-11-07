package com.fpoly.java6.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
// Khi API trả về json dòng nào bị null thì ở client sẽ không hiển thị thuộc tính đó
public class DataResponse<T> {
	private boolean success;
	private String message;
	private Map<String, List<String>> errors;
	private T data;
}

//{
//	success: true,
//	message: null => Không hiển thị ở API response 
//}

