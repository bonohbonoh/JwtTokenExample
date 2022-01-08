package com.example.JwtTokenExample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class LoginUserDto {

    @NotEmpty(message = "email을 입력하세요")
    private String email;

    @NotEmpty(message = "password를 입력하세요")
    private String password;

}
