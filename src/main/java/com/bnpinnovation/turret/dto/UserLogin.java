package com.bnpinnovation.turret.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class UserLogin {
    String username;
    String password;
}
