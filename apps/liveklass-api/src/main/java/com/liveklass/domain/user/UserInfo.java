package com.liveklass.domain.user;

public record UserInfo(
		Long id,
		String loginId,
		String userRole
) {
	public static UserInfo from(UserEntity user){
		return new UserInfo(
				user.getId(),
				user.getLoginId(),
				user.getUserRole().name()
		);
	}
}
