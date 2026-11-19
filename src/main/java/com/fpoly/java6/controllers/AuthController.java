package com.fpoly.java6.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fpoly.java6.beans.LoginBean;
import com.fpoly.java6.beans.RegisterBean;
import com.fpoly.java6.response.DataResponse;
import com.fpoly.java6.response.LoginResponse;
import com.fpoly.java6.services.AuthService;
import com.fpoly.java6.services.ErrorServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private ErrorServices errorServices;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<DataResponse<String>> register(
            @Valid @RequestBody(required = false) RegisterBean bean, Errors errors) {
        DataResponse<String> response = new DataResponse<String>();

        if (bean == null) {
            Map<String, List<String>> errorMap = new HashMap<>();
            errorMap.put("requestBody", Collections.singletonList("Request body không được để trống"));
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, List<String>> errorMap = errorServices.getError(errors);
        if (errorMap != null) {
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            authService.register(bean);
        } catch (ResponseStatusException e) {
            errorMap = new HashMap<>();
            errorMap.put("error", Collections.singletonList(e.getReason()));
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(e.getStatusCode()).body(response);
        }

        response.setSuccess(true);
        response.setMessage("Đăng ký thành công");
        response.setData(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResponse>> login(
            @Valid @RequestBody(required = false) LoginBean bean, Errors errors) {
        DataResponse<LoginResponse> response = new DataResponse<LoginResponse>();

        if (bean == null) {
            Map<String, List<String>> errorMap = new HashMap<>();
            errorMap.put("requestBody", Collections.singletonList("Request body không được để trống"));
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, List<String>> errorMap = errorServices.getError(errors);
        if (errorMap != null) {
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            response.setData(authService.login(bean));
        } catch (ResponseStatusException e) {
            errorMap = new HashMap<>();
            errorMap.put("error", Collections.singletonList(e.getReason()));
            response.setSuccess(false);
            response.setErrors(errorMap);
            response.setData(null);
            return ResponseEntity.status(e.getStatusCode()).body(response);
        }

        response.setSuccess(true);
        response.setMessage("Đăng nhập thành công");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
