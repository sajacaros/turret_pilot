package com.bnpinnovation.turret.dto;

import com.bnpinnovation.turret.security.JWTUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class VerifyResult {
    String username;
    boolean result;
    JWTUtil.TokenType.Origin origin;
}
