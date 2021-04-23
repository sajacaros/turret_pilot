package com.bnpinnovation.turret.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityMessage {
    private String message;
    @JsonIgnore
    private Authentication auth;
}
