package com.example.JwtTokenExample.service;

import com.example.JwtTokenExample.common.JwtTokenProvider;
import com.example.JwtTokenExample.dto.LoginUserDto;
import com.example.JwtTokenExample.dto.ReadUserInfoDto;
import com.example.JwtTokenExample.dto.SignUpUserDto;
import com.example.JwtTokenExample.dto.UserSearchDto;
import com.example.JwtTokenExample.entity.Member;
import com.example.JwtTokenExample.exception.RoleUnderprivilegedException;
import com.example.JwtTokenExample.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final JwtTokenProvider provider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public String getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Integer getMemberGrade(Member member) {
        return member.getRole().getValue();
    }

    public Long signUpUser(SignUpUserDto dto) {
        Optional<Member> member = memberRepository.findByEmail(dto.getEmail());
        dto.passwordEncoding(passwordEncoder.encode(dto.getPassword()));
        if (member.isPresent()) {
            throw new RuntimeException("이미 존재하는 회원입니다.");
        }
        Long memberId = memberRepository.save(dto.toEntity()).getMemberId();
        return memberId;
    }

    public String loginUser(LoginUserDto dto) {
        String email = dto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
        boolean isEqualPassword = passwordEncoder.matches(dto.getPassword(), member.getPassword());
        if (!isEqualPassword) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return provider.generateToken(email, Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())));
    }

    public ReadUserInfoDto readUserInfoDto() {
        String email = getAuthentication();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("인증되지 않은 회원입니다."));
        return new ReadUserInfoDto(member);
    }

    public UserSearchDto userSearchDto(Long memberId) {
        String email = getAuthentication();
        Member searchedMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member searchingMember = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("인증되지 않은 회원입니다."));
        if (getMemberGrade(searchingMember) < getMemberGrade(searchedMember)) {
            throw new RoleUnderprivilegedException("조회할 권한이 없습니다.");
        }
        return new UserSearchDto(searchedMember.getEmail(), searchedMember.getName());
    }

}
