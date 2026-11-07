package com.fpoly.java6.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

@Service
public class ErrorServices {

	public Map<String, List<String>> getError(Errors errors){
		
		if(!errors.hasErrors()) {
			return null;
		}
		
		Map<String, List<String>> errorMap = new HashMap<>();

        for (FieldError fieldError : errors.getFieldErrors()) { 
            String fieldName = fieldError.getField();
            String message = fieldError.getDefaultMessage();
        	
        	List<String> messages = errorMap.get(fieldName);
        	if(messages == null) {
        		messages = new ArrayList<String>();
        	}
        	
        	messages.add(message);
        	
        	errorMap.put(fieldName, messages);
        }
        
        return errorMap;
	}
}
