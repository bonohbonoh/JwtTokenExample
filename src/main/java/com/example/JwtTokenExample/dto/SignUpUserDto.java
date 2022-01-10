package com.example.JwtTokenExample.dto;

import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpUserDto {

    @NotEmpty(message = "email을 입력하세요")
    @Size(min = 6 ,max = 40,message = "이메일은 6글자 이상 40글자 미만으로 작성해주세요")
    @Email
    private String email;

    @NotEmpty(message = "password를 입력하세요")
    @Size(min = 4,max = 20,message = "비밀번호는 4글자 이상 20글자 미만으로 작성해 주세요.")
    private String password;

    @NotEmpty
    @Size(min = 2,max = 5,message = "이름은 2글자 이상 5글자 미만으로 작성해주세요.")
    private String name;

    @NotNull
    private Role role;

    @JsonIgnore
    public void passwordEncoding(String password) {
        this.password = password;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .role(role)
                .build();
    }
}
