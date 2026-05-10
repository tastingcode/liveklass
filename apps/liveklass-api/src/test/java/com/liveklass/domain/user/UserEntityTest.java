package com.liveklass.domain.user;

import com.liveklass.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserEntityTest {

	/**
	 * - 사용자는 로그인 ID와 역할로 생성된다.
	 * - 유저 생성 시 역할이 없으면 예외가 발생한다.
	 */
	@Nested
	@DisplayName("유저 생성")
	class Create{
		@DisplayName("사용자는 로그인 ID와 역할로 생성된다.")
		@Test
		public void 사용자는_로그인_ID와_역할로_생성된다() {
		    //given
			String loginId = "asd123";
			String userRole = "CREATOR";
			UserCommand.Create command = new UserCommand.Create(loginId, userRole);

			//when
			UserEntity user = UserEntity.from(command);

			//then
			assertThat(user.getLoginId()).isEqualTo(loginId);
			assertThat(user.getUserRole()).isEqualTo(UserRole.from(userRole));

		}
		
		@DisplayName("유저 생성 시 역할이 없으면 예외가 발생한다.")
		@ParameterizedTest
		@ValueSource(strings = {
				"","asd"
		})
		public void 유저_생성_시_역할이_없으면_예외가_발생한다(String userRole) {
		    //given
			String loginId = "asd123";
			UserCommand.Create command = new UserCommand.Create(loginId, userRole);
		    
		    //then
			assertThrows(CoreException.class, () -> {
				UserEntity.from(command);
			});

		}
	}

}
