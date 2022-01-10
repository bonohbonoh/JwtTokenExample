package com.example.JwtTokenExample.controller;


import com.example.JwtTokenExample.common.JwtTokenProvider;
import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.dto.LoginUserDto;
import com.example.JwtTokenExample.dto.SignUpUserDto;
import com.example.JwtTokenExample.entity.Member;
import com.example.JwtTokenExample.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTest {

    private static final String EMAIL = "guest_email@abc.com";
    private static final String NAME = "gName";
    private static final String PASSWORD = "guest_password";
    private static final Role guest = Role.GUEST;
    private static final Role user = Role.USER;
    private static final String USER_EMAIL = "user_email@abc.com";
    private static final String USER_NAME = "uName";
    private static final String USER_PASSWORD = "user_password";
    private static final String URL = "/api/v1/member/";
    private static String TOKEN = "";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberController memberController;

    @Autowired
    private JwtTokenProvider provider;

    @BeforeTransaction
    public void clearSecurity(){
        SecurityContextHolder.clearContext();
    }

    @BeforeAll
    public void settingMvcBuilder(){
        mvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .build();
    }

    public void deleteAll(){
        memberRepository.deleteAll();
    }

    @BeforeAll
    public void initUser(){
        String initPassword = passwordEncoder.encode(USER_PASSWORD);
        Member member = Member.builder()
                .email(USER_EMAIL)
                .name(USER_NAME)
                .password(initPassword)
                .role(user).build();
        memberRepository.save(member);
    }

    @BeforeAll
    public void initGuest(){
        String initPassword = passwordEncoder.encode(PASSWORD);
        Member member = Member.builder()
                .email(EMAIL)
                .name(NAME)
                .password(initPassword)
                .role(guest).build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("게스트 권한 회원가입 테스트")
    public void signUpControllerTest()throws Exception{

        //given
        String content = objectMapper.writeValueAsString(new SignUpUserDto("G"+EMAIL, PASSWORD, NAME, guest));

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
        assertThat(result.getResponse().getContentAsString()).isNotNull();
    }

    @Test
    @DisplayName("유저 권한 회원가입 테스트")
    public void signUpUserControllerTest() throws Exception{

        //given
        String content = objectMapper.writeValueAsString(new SignUpUserDto("U"+USER_EMAIL,USER_PASSWORD,USER_NAME,user));

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
    //given
    @WithUserDetails(USER_EMAIL)
    @DisplayName("회원 정보 조회")
    @Order(1)
    public void myPageControllerTest() throws Exception{

        //when
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get(URL + "auth/my-page")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();
        assertThat(result.getResponse()).isNotNull();
    }

}
