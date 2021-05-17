package com.bnpinnovation.turret.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class UserLogin {
    String username;
    String password;
}
