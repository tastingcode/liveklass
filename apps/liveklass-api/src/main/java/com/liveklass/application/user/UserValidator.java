package com.liveklass.application.user;

import com.liveklass.domain.user.UserCommand;
import com.liveklass.domain.user.UserInfo;
import com.liveklass.domain.user.UserRole;
import com.liveklass.domain.user.UserService;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserValidator {
	private final UserService userService;

	public void validateStudent(Long userId) {
		UserInfo userInfo = userService.findUser(new UserCommand.Find(userId))
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));

		if (!UserRole.STUDENT.name().equals(userInfo.userRole())) {
			throw new CoreException(ErrorType.FORBIDDEN, "수강생만 수강 신청 기능을 사용할 수 있습니다.");
		}
	}
}
