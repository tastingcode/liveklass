package com.liveklass.interfaces.api.user;

import com.liveklass.application.user.UserCriteria;
import com.liveklass.application.user.UserFacade;
import com.liveklass.application.user.UserResult;
import com.liveklass.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

	private final UserFacade userFacade;

	@PostMapping("")
	@Override
	public ApiResponse<UserV1Dto.UserResponse> joinUser(@Valid @RequestBody UserV1Dto.JoinRequest request) {
		UserCriteria.Join criteria = request.toCriteria();
		UserResult userResult = userFacade.joinUser(criteria);
		UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userResult);
		return ApiResponse.success(response);
	}

	@GetMapping("/me")
	@Override
	public ApiResponse<UserV1Dto.UserResponse> getMyInfo(@RequestHeader("X-USER-ID") Long userId) {
		UserResult userResult = userFacade.getUser(new UserCriteria.Get(userId));
		UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userResult);
		return ApiResponse.success(response);
	}

	@PostMapping("/login")
	@Override
	public ApiResponse<UserV1Dto.UserResponse> login(@Valid @RequestBody UserV1Dto.LoginRequest request) {
		UserCriteria.Login criteria = request.toCriteria();
		UserResult userResult = userFacade.login(criteria);
		UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userResult);
		return ApiResponse.success(response);
	}
}
