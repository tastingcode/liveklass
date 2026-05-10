package com.liveklass.application.user;

import com.liveklass.domain.user.UserCommand;

public class UserCriteria {
	public record Join(String loginId, String userRole) {
		public UserCommand.Create toUserCreate() {
			return new UserCommand.Create(
					loginId,
					userRole
			);
		}
	}

	public record Get(Long userId) {
		public UserCommand.Find toUserFind() {
			return new UserCommand.Find(userId);
		}

	}
}
