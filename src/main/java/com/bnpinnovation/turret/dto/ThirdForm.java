package com.bnpinnovation.turret.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class ThirdForm {
	@Setter
	@Getter
	@ToString
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class New {
		private String symbol;
		private String name;
		private Long lifeTime; // s
	}

	@Setter
	@Getter
	@ToString
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class ThirdDetails {
		private Long thirdId;
		private String symbol;
		private String name;
		private String accessToken;
		private Long lifeTime;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime expiredDate;
		private boolean enabled;
	}
}
