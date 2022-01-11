package com.example.JwtTokenExample.service;

import com.example.JwtTokenExample.common.JwtTokenProvider;
import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.dto.LoginUserDto;
import com.example.JwtTokenExample.dto.ReadUserInfoDto;
import com.example.JwtTokenExample.dto.SignUpUserDto;
import com.example.JwtTokenExample.dto.UserSearchDto;
import com.example.JwtTokenExample.entity.Member;
import com.example.JwtTokenExample.exception.RoleUnderprivilegedException;
import com.example.JwtTokenExample.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceTest {

    private static final String EMAIL = "guest_email@abc.com";
    private static final String NAME = "gName";
    private static final String PASSWORD = "guest_password";
    private static final String USER_EMAIL = "user_email@abc.com";
    private static final String USER_NAME = "uName";
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider provider;

    @BeforeAll
    public void initUser() {
        String password = passwordEncoder.encode(PASSWORD);
        Member member = Member.builder()
                .email(USER_EMAIL)
                .password(password)
                .name(USER_NAME)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }

    @BeforeAll
    public void initGuest() {
        String password = passwordEncoder.encode(PASSWORD);
        Member member = Member.builder()
                .email(EMAIL)
                .password(password)
                .name(NAME)
                .role(Role.GUEST)
                .build();
        memberRepository.save(member);
    }

    @Test
    public void signUpServiceTest() {
        memberRepository.deleteAll();

        //given
        SignUpUserDto dto = new SignUpUserDto(EMAIL, PASSWORD, NAME, Role.USER);

        //when
        Long memberId = memberService.signUpUser(dto);

        //then
        assertThat(memberRepository.findByMemberId(memberId)).isPresent();
    }

    @Test
    public void loginServiceTest() {

        //given
        LoginUserDto dto = new LoginUserDto(EMAIL, PASSWORD);

        //when
        String token = memberService.loginUser(dto);

        //then
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    //given
    @WithUserDetails(USER_EMAIL)
    public void readUserServiceTest() {

        //when
        ReadUserInfoDto dto = memberService.readUserInfoDto();

        //then
        assertThat(dto.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(dto.getName()).isEqualTo(USER_NAME);
        assertThat(dto.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @WithUserDetails(USER_EMAIL)
    public void searchUserServiceTest() {

        //given
        Long searchedMemberId = memberRepository.findByEmail(EMAIL).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다.")).getMemberId();

        //when
        UserSearchDto dto = memberService.userSearchDto(searchedMemberId);

        //then
        assertThat(dto.getEmail()).isEqualTo(EMAIL);
        assertThat(dto.getName()).isEqualTo(NAME);
    }

    @Test
    @WithUserDetails(EMAIL)
    public void searchUserFailServiceTest() {

        //given
        Long searchedMemberId = memberRepository.findByEmail(USER_EMAIL).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다.")).getMemberId();

        //when
        RoleUnderprivilegedException e = assertThrows(RoleUnderprivilegedException.class, () -> memberService.userSearchDto(searchedMemberId));

        //then
        assertThat(e.getMessage()).isEqualTo("조회할 권한이 없습니다.");
    }
}
