package com.example.JwtTokenExample.dto;

import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadUserInfoDto {

    private String email;

    private String password;

    private String name;

    private Role role;

    public ReadUserInfoDto(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.role = member.getRole();
    }
}
