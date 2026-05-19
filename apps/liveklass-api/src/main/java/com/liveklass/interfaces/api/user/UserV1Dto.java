package com.liveklass.interfaces.api.user;

import com.liveklass.application.user.UserCriteria;
import com.liveklass.application.user.UserResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserV1Dto {
	public record JoinRequest(
			@NotNull(message = "로그인 ID는 필수입니다.")
			@Size(max = 10, message = "ID는 10자 이내이어야 합니다.")
			@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영문 및 숫자만 포함할 수 있습니다.")
			String loginId,

			@NotNull(message = "역할은 필수입니다.")
			String userRole
	) {
		public UserCriteria.Join toCriteria() {
			return new UserCriteria.Join(
					loginId,
					userRole
			);
		}
	}

	public record UserResponse(Long id, String loginId, String userRole) {
		public static UserResponse from(UserResult userResult) {
			return new UserResponse(
					userResult.id(),
					userResult.loginId(),
					userResult.userRole()
			);
		}
	}

	public record LoginRequest(
			@NotNull(message = "로그인 ID는 필수입니다.")
			@Size(max = 10, message = "ID는 10자 이내이어야 합니다.")
			@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영문 및 숫자만 포함할 수 있습니다.")
			String loginId
	) {
		public UserCriteria.Login toCriteria() {
			return new UserCriteria.Login(loginId);
		}
	}
}
