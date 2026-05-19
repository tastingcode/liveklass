package com.liveklass.domain.user;

public class UserCommand {
	public record Create(String loginId, String userRole){
		public UserRole toUserRole(){
			return UserRole.from(userRole);
		}
	}

	public record Find(Long userId) {
	}

	public record Login(String loginId) {
	}
}
