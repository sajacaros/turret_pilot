package com.bnpinnovation.turret.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class VerifyResult {
    String username;
    boolean result;
}
