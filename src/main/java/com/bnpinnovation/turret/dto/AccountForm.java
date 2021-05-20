package com.bnpinnovation.turret.dto;

import lombok.*;

import java.util.List;

public class AccountForm {
	@Setter
	@Getter
	@ToString
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class NewAccount {
		private String username;
		private String password;
		private String name;
		private List<String> roles;
	}
}
