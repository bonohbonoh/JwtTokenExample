package com.example.JwtTokenExample.config;

import com.example.JwtTokenExample.common.JwtFilter;
import com.example.JwtTokenExample.common.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .httpBasic().disable();

        http
                .headers()
                .frameOptions()
                .sameOrigin();

        http
                .authorizeRequests()
                .antMatchers("/h2-console/*").permitAll()
                .antMatchers("/swagger-ui/**/").permitAll()
                .antMatchers("/swagger/**/").permitAll()
                .antMatchers("/api/v1/member/sign-up").permitAll()
                .antMatchers("/api/v1/member/login").anonymous()
                .antMatchers("/api/v1/member/auth/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin().disable()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
