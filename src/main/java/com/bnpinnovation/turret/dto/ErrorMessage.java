package com.bnpinnovation.turret.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class ErrorMessage {
    private final long code;
    private final String message;
}
