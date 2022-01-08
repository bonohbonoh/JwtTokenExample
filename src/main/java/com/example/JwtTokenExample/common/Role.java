package com.example.JwtTokenExample.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", 1),
    USER("ROLE_USER", 2);

    private final String key;
    private final Integer value;
}
