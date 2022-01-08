package com.example.JwtTokenExample.controller;

import com.example.JwtTokenExample.dto.LoginUserDto;
import com.example.JwtTokenExample.dto.ReadUserInfoDto;
import com.example.JwtTokenExample.dto.SignUpUserDto;
import com.example.JwtTokenExample.dto.UserSearchDto;
import com.example.JwtTokenExample.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/sign-up")
    public ResponseEntity<Long> signUpUser(@RequestBody @Valid SignUpUserDto dto) {
        return new ResponseEntity<Long>(memberService.signUpUser(dto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginUserDto dto) {
        return new ResponseEntity<String>(memberService.loginUser(dto), HttpStatus.OK);
    }

    @GetMapping(value = "/auth/my-page")
    public ResponseEntity<ReadUserInfoDto> readUserInfoDtoResponseEntity() {
        return new ResponseEntity<ReadUserInfoDto>(memberService.readUserInfoDto(), HttpStatus.OK);
    }

    @GetMapping(value = "/auth/member/{memberId}")
    public ResponseEntity<UserSearchDto> readUserInfoDtoResponseEntity(@PathVariable Long memberId) {
        return new ResponseEntity<UserSearchDto>(memberService.userSearchDto(memberId), HttpStatus.OK);
    }

}
