package com.liveklass.domain.course;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseEntityTest {

	/**
	 * - 강의 생성 시 DRAFT로 생성된다.
	 * - 강의 생성 시 가격이 음수이면 실패한다.
	 */
	@Nested
	@DisplayName("강의 생성")
	class Create{
		@DisplayName("강의 생성 시 DRAFT로 생성된다.")
		@Test
		public void 강의_생성_시_DRAFT로_생성된다() {
		    //given
			CourseCommand.Create createCommand = new CourseCommand.Create(1L, "asd", "asd", 1000, 30, LocalDate.now(), LocalDate.now().plusDays(30));
			CourseStatus targetStatus = CourseStatus.DRAFT;

			//when
			CourseEntity courseEntity = CourseEntity.from(createCommand);

			//then
			assertThat(courseEntity.getStatus()).isEqualTo(targetStatus);
		}

		@DisplayName("강의 생성 시 가격이 음수이면 실패한다.")
		@Test
		public void 강의_생성_시_가격이_음수이면_실패한다(){
			//given
			CourseCommand.Create createCommand = new CourseCommand.Create(1L, "asd", "asd", -1000, 30, LocalDate.now(), LocalDate.now().plusDays(30));

			//then
			CoreException coreException = assertThrows(CoreException.class, () -> {
				CourseEntity.from(createCommand);
			});

			assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
		}
	}

	/**
	 * - 강의 상태 OPEN 시 상태는 OPEN으로 변경된다.
	 * - 강의 상태 CLOSE 시 상태는 CLOSED로 변경된다.
	 */
	@Nested
	@DisplayName("강의 상태 변경")
	class Status{
		@DisplayName("강의 상태 OPEN 시 상태는 OPEN으로 변경된다.")
		@Test
		public void 강의_상태_OPEN_시_상태는_OPEN으로_변경된다() {
		    //given
			CourseCommand.Create createCommand = new CourseCommand.Create(1L, "asd", "asd", 1000, 30, LocalDate.now(), LocalDate.now().plusDays(30));
			
		    //when
			CourseEntity course = CourseEntity.from(createCommand);
			course.open();
			
			//then
			assertThat(course.getStatus()).isEqualTo(CourseStatus.OPEN);
		}

		@DisplayName("강의 상태 CLOSE 시 상태는 CLOSED로 변경된다.")
		@Test
		public void 강의_상태_CLOSE_시_상태는_CLOSED로_변경된다() {
			//given
			CourseCommand.Create createCommand = new CourseCommand.Create(1L, "asd", "asd", 1000, 30, LocalDate.now(), LocalDate.now().plusDays(30));

			//when
			CourseEntity course = CourseEntity.from(createCommand);
			course.close();

			//then
			assertThat(course.getStatus()).isEqualTo(CourseStatus.CLOSED);
		}
	}

}
