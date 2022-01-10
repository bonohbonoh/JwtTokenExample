package com.example.JwtTokenExample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoleUnderprivilegedExceptionHandler {
    @ExceptionHandler(RoleUnderprivilegedException.class)
    public ResponseEntity<ExceptionStatus> roleUnderprivileged(RoleUnderprivilegedException roleUnderprivilegedException) {
        ExceptionStatus response = new ExceptionStatus(roleUnderprivilegedException.getMessage(), "403");
        return new ResponseEntity<ExceptionStatus>(response, HttpStatus.FORBIDDEN);
    }
}
