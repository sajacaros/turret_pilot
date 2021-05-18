package com.bnpinnovation.turret.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class VerifyResult {
    String username;
    boolean result;
}
