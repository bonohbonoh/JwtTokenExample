package com.example.JwtTokenExample.common;

import com.example.JwtTokenExample.service.MemberDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("spring.jwt.secret")
    private String key;
    private long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final MemberDetailsService memberDetailsService;

    @PostConstruct
    protected void init() {
        key = Base64.getEncoder().encodeToString(key.getBytes());
    }

    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("ROLE", authorities);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}