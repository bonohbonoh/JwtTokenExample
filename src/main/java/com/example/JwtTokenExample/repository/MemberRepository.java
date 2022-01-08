package com.example.JwtTokenExample.repository;

import com.example.JwtTokenExample.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberId(Long memberId);
}
