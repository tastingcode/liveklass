package com.liveklass.domain.user;

public class UserCommand {
	public record Create(String loginId, String role){
		public UserRole toUserRole(){
			return UserRole.from(role);
		}
	}
}
