package com.bnpinnovation.turret.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class AccountForm {
	@Setter
	@Getter
	@ToString
	@NoArgsConstructor
	public static class NewAccount {
		private String username;
		private String password;
		private String name;
		private String role;
	}
}
