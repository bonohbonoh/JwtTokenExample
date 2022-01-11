package com.example.JwtTokenExample.entity;

import com.example.JwtTokenExample.common.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 10)
    private Role role;

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateName(String name){
        this.name = name;
    }

    @Builder
    public Member(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}
