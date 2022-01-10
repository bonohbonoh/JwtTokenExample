package com.example.JwtTokenExample.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionStatus {

    private String msg;

    private String code;
}
