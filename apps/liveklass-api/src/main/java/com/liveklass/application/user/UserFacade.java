package com.liveklass.application.user;

import com.liveklass.domain.user.UserInfo;
import com.liveklass.domain.user.UserService;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserFacade {
	private final UserService userService;

	@Transactional
	public UserResult joinUser(UserCriteria.Join criteria){
		UserInfo userInfo = userService.create(criteria.toUserCreate());

		return UserResult.of(userInfo);
	}

	@Transactional(readOnly = true)
	public UserResult getUser(UserCriteria.Get criteria) {
		UserInfo userInfo = userService.findUser(criteria.toUserFind()).orElseThrow(() -> new CoreException(
				ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: " + criteria.userId()
		));

		return UserResult.of(userInfo);
	}

	@Transactional(readOnly = true)
	public UserResult login(UserCriteria.Login criteria) {
		UserInfo userInfo = userService.login(criteria.toUserLogin()).orElseThrow(() -> new CoreException(
				ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: " + criteria.loginId()
		));

		return UserResult.of(userInfo);
	}
}
