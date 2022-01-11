package com.example.JwtTokenExample.repository;

import com.example.JwtTokenExample.common.Role;
import com.example.JwtTokenExample.entity.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryTest {

    private static final String EMAIL = "guest_email@abc.com";
    private static final String NAME = "gName";
    private static final String PASSWORD = "guest_password";
    private static final String UPDATE_NAME = "uName";
    private static final String UPDATE_PASSWORD = "user_password";

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    public void initUser() {
        Member member = Member.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("유저 저장 테스트")
    public void memberSaveTest() {
        memberRepository.deleteAll();

        //given
        Member member = Member.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .role(Role.USER)
                .build();

        //when
        Long memberId = memberRepository.save(member).getMemberId();

        //then
        Member saveMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        assertThat(saveMember.getMemberId()).isEqualTo(memberId);
        assertThat(saveMember.getEmail()).isEqualTo(EMAIL);
        assertThat(saveMember.getPassword()).isEqualTo(PASSWORD);
        assertThat(saveMember.getName()).isEqualTo(NAME);
        assertThat(saveMember.getRole().getValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저 아이디로 검색 테스트")
    public void memberFindTest() {

        //given
        Long memberId = 1L;

        //when
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        //then
        assertThat(member.getMemberId()).isEqualTo(memberId);
        assertThat(member.getEmail()).isEqualTo(EMAIL);
        assertThat(member.getPassword()).isEqualTo(PASSWORD);
        assertThat(member.getName()).isEqualTo(NAME);
        assertThat(member.getRole().getValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저 정보 수정 테스트")
    public void memberUpdateTest() {

        //given
        Long memberId = 1L;
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        //when
        member.updatePassword(UPDATE_PASSWORD);
        member.updateName(UPDATE_NAME);
        Long updateMemberId = memberRepository.save(member).getMemberId();

        //then
        Member updateMember = memberRepository.findByMemberId(updateMemberId).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        assertThat(updateMember.getMemberId()).isEqualTo(memberId);
        assertThat(updateMember.getEmail()).isEqualTo(EMAIL);
        assertThat(updateMember.getPassword()).isEqualTo(UPDATE_PASSWORD);
        assertThat(updateMember.getName()).isEqualTo(UPDATE_NAME);
        assertThat(updateMember.getRole().getValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    public void memberDeleteTest() {

        //given
        Long memberId = 1L;

        //when
        memberRepository.deleteById(memberId);

        //then
        assertThat(memberRepository.findByMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("유저 이메일로 검색 테스트")
    public void memberFindEmailTest() {

        //given
        String email = EMAIL;

        //when
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        //then
        assertThat(member.getEmail()).isEqualTo(EMAIL);
        assertThat(member.getPassword()).isEqualTo(PASSWORD);
        assertThat(member.getName()).isEqualTo(NAME);
        assertThat(member.getRole().getValue()).isEqualTo(2);
    }

}
