package com.fpoly.java6.beans;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterBean {
    @NotBlank(message = "Tên là bắt buộc")
    @Size(max = 155)
    private String name;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, max = 72, message = "Mật khẩu phải dài từ 8 đến 72 ký tự")
    private String password;
}
