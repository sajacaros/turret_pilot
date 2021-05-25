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
    public boolean isLogin() {
        return this.type.isLogin();
    }

    public boolean isRefresh() {
        return this.type.isRefresh();
    }

    public enum Type {
        LOGIN,REFRESH;

        public boolean isLogin() {
            return LOGIN.equals(this);
        }

        public boolean isRefresh() {
            return REFRESH.equals(this);
        }
    }

    private Type type;
    String username;
    String password;
    String refreshToken;
}
