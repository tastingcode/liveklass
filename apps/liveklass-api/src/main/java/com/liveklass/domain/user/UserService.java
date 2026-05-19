package com.liveklass.domain.user;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public UserInfo create(UserCommand.Create command) {
		if (userRepository.existsByLoginId(command.loginId())) {
			throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID입니다.");
		}

		UserEntity user = UserEntity.from(command);

		return UserInfo.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public Optional<UserInfo> findUser(UserCommand.Find command){
		return userRepository.findById(command.userId()).map(UserInfo :: from);
	}

	@Transactional(readOnly = true)
	public Optional<UserInfo> login(UserCommand.Login command) {
		return userRepository.findByLoginId(command.loginId()).map(UserInfo :: from);
	}

}
