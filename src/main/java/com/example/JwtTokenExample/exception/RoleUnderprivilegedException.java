package com.example.JwtTokenExample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Role underprivileged Exception")
public class RoleUnderprivilegedException extends RuntimeException {
    public RoleUnderprivilegedException(String msg) {
        super(msg);
    }
}
