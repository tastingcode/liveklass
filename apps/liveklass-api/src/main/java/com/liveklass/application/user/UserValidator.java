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

	public void validateStudent(UserCommand.Find command) {
		UserInfo userInfo = userService.findUser(command)
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: " + command.userId()));

		if (!UserRole.STUDENT.name().equals(userInfo.userRole())) {
			throw new CoreException(ErrorType.BAD_REQUEST, "현재 사용자는 수강생이 아닙니다.");
		}
	}
}
