package com.liveklass.application.user;


import com.liveklass.domain.user.UserInfo;

public record UserResult(
		Long id,
		String loginId,
		String userRole
) {
	public static UserResult of(UserInfo userInfo){
		return new UserResult(
				userInfo.id(),
				userInfo.loginId(),
				userInfo.userRole()
		);
	}
}
