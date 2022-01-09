package com.example.JwtTokenExample.controller;


import com.example.JwtTokenExample.common.JwtTokenProvider;
import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.dto.LoginUserDto;
import com.example.JwtTokenExample.dto.SignUpUserDto;
import com.example.JwtTokenExample.entity.Member;
import com.example.JwtTokenExample.repository.MemberRepository;
import com.example.JwtTokenExample.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ControllerTest {

    private static final String EMAIL = "email@abc.com";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final Role guest = Role.GUEST;
    private static final Role user = Role.GUEST;
    private static final String UPDATE_EMAIL = "update_email@abc.com";
    private static final String UPDATE_NAME = "uName";
    private static final String UPDATE_PASSWORD = "update_password";
    private static final String URL = "/api/v1/member/";
    private static String TOKEN = "";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider provider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    public void deleteAll(){
        memberRepository.deleteAll();
    }

    @BeforeEach
    public void initUser(){
        String initPassword = passwordEncoder.encode(PASSWORD);
        Member member = Member.builder()
                .email(EMAIL)
                .name(NAME)
                .password(initPassword)
                .role(user).build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("게스트 권한 회원가입 테스트")
    public void signUpControllerTest()throws Exception{
        deleteAll();

        //given
        String content = objectMapper.writeValueAsString(new SignUpUserDto(EMAIL, PASSWORD, NAME, guest));

        //when
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post(URL+"sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .accept(MediaType.APPLICATION_JSON)
                )
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andDo(print())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isNotNull();
    }

    @Test
    @DisplayName("유저 권한 회원가입 테스트")
    public void signUpUserControllerTest() throws Exception{

        //given
        String content = objectMapper.writeValueAsString(new SignUpUserDto(UPDATE_EMAIL,PASSWORD,NAME,user));

        //when
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(URL+"sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).isNotNull();
    }

    @Test
    @DisplayName("유저 로그인 테스트")
    public void loginUserControllerTest() throws Exception{

        //given
        String content = objectMapper.writeValueAsString(new LoginUserDto(EMAIL,PASSWORD));

        //when
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(URL+"login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();

        //then
        TOKEN = result.getResponse().getContentAsString();
        assertThat(provider.validateToken(TOKEN)).isTrue();
    }

    @Test
    @DisplayName("회원 정보 조회")
    public void myPageControllerTest() throws Exception{


        //when
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(URL + "auth/my-page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN",TOKEN)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();
        assertThat(result.getResponse()).isNotNull();
    }

}
