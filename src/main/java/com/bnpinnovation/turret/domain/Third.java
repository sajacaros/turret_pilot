package com.bnpinnovation.turret.domain;

import com.bnpinnovation.turret.dto.ThirdForm;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Third extends TimeEntity {
    @Id
    @Column(name="THIRD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String symbol;
    private String name;
    private String accessToken;
    private Long lifeTime;
    private LocalDateTime expiredDate;
    private boolean enabled;  // 계정 활성화 여부

    @Builder
    public Third(
            String symbol,
            String name,
            String accessToken,
            Long lifeTime,
            LocalDateTime expiredDate
    ) {
        this.symbol = symbol;
        this.name = name;
        this.accessToken = accessToken;
        this.lifeTime = lifeTime;
        this.expiredDate = expiredDate;
        this.enabled = true;
    }

    public UserDetails generateUserDetails() {
        return new User(symbol,null,enabled,enabled,enabled,enabled,null);
    }

    public ThirdForm.ThirdDetails constructDetailsDto() {
        return ThirdForm.ThirdDetails.builder()
                .thirdId(id)
                .name(name)
                .symbol(symbol)
                .accessToken(accessToken)
                .lifeTime(lifeTime)
                .expiredDate(expiredDate)
                .enabled(enabled)
                .build();

    }

    public boolean valid(String token) {
        return enabled && this.accessToken.equals(token);
    }

    public void refresh(String accessToken, LocalDateTime expiredDate) {
        this.accessToken = accessToken;
        this.expiredDate = expiredDate;
    }

    public String symbol() {
        return this.symbol;
    }

    public long lifeTime() {
        return this.lifeTime;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}
