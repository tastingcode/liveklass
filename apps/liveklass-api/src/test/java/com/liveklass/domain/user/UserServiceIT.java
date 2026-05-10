package com.liveklass.domain.user;

import com.liveklass.support.error.CoreException;
import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceIT {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DatabaseCleanUp databaseCleanUp;

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}


	/**
	 * - 이미 가입된 ID 로 회원가입 시도 시, 실패한다.
	 * - 회원 가입 시 유저 정보가 반환된다.
	 */
	@Nested
	@DisplayName("회원 가입 시")
	class register {
		@DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
		@Test
		public void 이미_가입된_ID_로_회원가입_시도_시_실패한다() {
			//given
			String existsId = "existsId";
			UserCommand.Create command1 = new UserCommand.Create(existsId, "CREATOR");
			userService.create(command1);

			//when
			UserCommand.Create command2 = new UserCommand.Create(existsId, "CREATOR");

			//then
			assertThrows(CoreException.class, () -> {
				userService.create(command2);
			});

		}

		@DisplayName("회원 가입 시 유저 정보가 반환된다.")
		@Test
		public void 회원_가입_시_유저_정보가_반환된다() {
			//given
			UserCommand.Create command = new UserCommand.Create("asd123", "CREATOR");

			//when
			UserInfo userInfo = userService.create(command);

			//then
			assertAll(
					() -> assertThat(userInfo.loginId()).isEqualTo(command.loginId()),
					() -> assertThat(userInfo.userRole()).isEqualTo(command.userRole())
			);
		}
	}

	/**
	 * 해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
	 * 해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
	 */
	@DisplayName("유저 정보 조회")
	@Nested
	class Find {
		@DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
		@Test
		public void 해당_ID_의_회원이_존재할_경우_회원_정보가_반환된다() {
			//given
			UserCommand.Create command = new UserCommand.Create("asd123", "CREATOR");
			UserEntity user = UserEntity.from(command);

			//when
			UserEntity savedUser = userRepository.save(user);
			UserCommand.Find find = new UserCommand.Find(savedUser.getId());
			Optional<UserInfo> findUser = userService.findUser(find);

			//then
			assertAll(
					() -> assertThat(findUser).isNotEmpty(),
					() -> assertThat(findUser.get().id()).isEqualTo(savedUser.getId())
			);

		}

		@DisplayName("해당 ID 의 회원이 존재하지 않을 경우, 빈 Optional 이 반환된다.")
		@Test
		public void 해당_ID_의_회원이_존재하지_않을_경우_빈_Optional_이_반환된다() {
			//given
			UserCommand.Find find = new UserCommand.Find(-1L);

			//when
			Optional<UserInfo> findUser = userService.findUser(find);

			//then
			assertThat(findUser).isEmpty();

		}
	}


}
