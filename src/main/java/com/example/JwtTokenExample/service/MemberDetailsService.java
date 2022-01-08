package com.example.JwtTokenExample.service;

import com.example.JwtTokenExample.entity.Member;
import com.example.JwtTokenExample.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new User(member.getEmail(), member.getPassword(), Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())));
    }
}