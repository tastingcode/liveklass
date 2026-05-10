package com.liveklass.domain.user;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;

	@Mock
	UserRepository userRepository;

	/**
	 * - 이미 존재하는 ID가 주어지면, CONFLICT 예외가 발생한다.
	 * - 유저 생성 시 유저 정보가 반환된다.
	 */
	@Nested
	@DisplayName("유저 생성 시")
	class Create {
		@DisplayName("이미 존재하는 ID가 주어지면, CONFLICT 예외가 발생한다.")
		@Test
		public void 이미_존재하는_ID가_주어지면_CONFLICT_예외가_발생한다() {
			//given
			UserCommand.Create command = new UserCommand.Create("asd123", "CREATOR");
			given(userRepository.existsByLoginId(command.loginId()))
					.willReturn(true);

			assertThatThrownBy(() -> userService.create(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.CONFLICT);
		}

		@DisplayName("유저 생성 시 유저 정보가 반환된다.")
		@Test
		public void 유저_생성_시_유저_정보가_반환된다() {
			//given
			UserCommand.Create command = new UserCommand.Create("asd123", "CREATOR");
			given(userRepository.existsByLoginId(command.loginId()))
					.willReturn(false);

			given(userRepository.save(any(UserEntity.class)))
					.willAnswer(invocation -> invocation.getArgument(0));

			//when
			UserInfo savedUserInfo = userService.create(command);

			//then
			Assertions.assertAll(
					() -> assertThat(savedUserInfo.loginId()).isEqualTo(command.loginId()),
					() -> assertThat(savedUserInfo.userRole()).isEqualTo(command.userRole())
			);

		}
	}

}
