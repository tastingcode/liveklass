package com.liveklass.domain.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
	@InjectMocks
	private CourseService courseService;

	@Mock
	private CourseRepository courseRepository;

	/**
	 * - 강의 생성 시 강의 정보가 반환된다.
	 */
	@DisplayName("강의 생성 시 강의 정보가 반환된다.")
	@Test
	void 강의_생성_시_강의_정보가_반환된다() {
		//given
		CourseCommand.Create command = createCommand(1L, "자바 기초");
		given(courseRepository.save(any(CourseEntity.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

		//when
		CourseInfo courseInfo = courseService.create(command);

		//then
		assertThat(courseInfo.title()).isEqualTo(command.title());
		assertThat(courseInfo.status()).isEqualTo(CourseStatus.DRAFT.name());
	}

	/**
	 * - 해당 ID의 강의가 존재할 경우 강의 정보가 반환된다.
	 */
	@Nested
	@DisplayName("강의 조회")
	class Find {
		@DisplayName("해당 ID의 강의가 존재할 경우 강의 정보가 반환된다.")
		@Test
		void 해당_ID의_강의가_존재할_경우_강의_정보가_반환된다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			given(courseRepository.findById(1L)).willReturn(Optional.of(course));

			//when
			Optional<CourseInfo> courseInfo = courseService.findCourseInfo(new CourseCommand.Find(1L));

			//then
			assertThat(courseInfo).isPresent();
			assertThat(courseInfo.get().title()).isEqualTo(course.getTitle());
		}
	}

	/**
	 * - 상태값이 없으면 OPEN 상태의 강의 목록이 반환된다.
	 * - 상태값이 주어지면 해당 상태의 강의 목록이 반환된다.
	 */
	@Nested
	@DisplayName("강의 목록 조회")
	class FindCourses {
		@DisplayName("상태값이 없으면 OPEN 상태의 강의 목록이 반환된다.")
		@Test
		void 상태값이_없으면_OPEN_상태의_강의_목록이_반환된다() {
			//given
			CourseCommand.Search command = new CourseCommand.Search("OPEN", 0, 10);
			CourseEntity openCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			openCourse.open();
			given(courseRepository.findAllByStatus(CourseStatus.OPEN, command.page(), command.size()))
					.willReturn(List.of(openCourse));

			//when
			List<CourseInfo> courses = courseService.findCourses(command);

			//then
			assertThat(courses).hasSize(1);
			assertThat(courses.getFirst().status()).isEqualTo(CourseStatus.OPEN.name());
		}

		@DisplayName("상태값이 주어지면 해당 상태의 강의 목록이 반환된다.")
		@Test
		void 상태값이_주어지면_해당_상태의_강의_목록이_반환된다() {
			//given
			CourseCommand.Search command = new CourseCommand.Search("DRAFT", 0, 10);
			CourseEntity draftCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			given(courseRepository.findAllByStatus(CourseStatus.DRAFT, command.page(), command.size()))
					.willReturn(List.of(draftCourse));

			//when
			List<CourseInfo> courses = courseService.findCourses(command);

			//then
			assertThat(courses).hasSize(1);
			assertThat(courses.getFirst().status()).isEqualTo(CourseStatus.DRAFT.name());
		}

		@DisplayName("상태값이 없으면 OPEN 상태의 강의 수가 반환된다.")
		@Test
		void 상태값이_없으면_OPEN_상태의_강의_수가_반환된다() {
			//given
			CourseCommand.Search command = new CourseCommand.Search("OPEN", 0, 10);
			given(courseRepository.countByStatus(CourseStatus.OPEN, command.page(), command.size())).willReturn(3L);

			//when
			long count = courseService.countCourses(command);

			//then
			assertThat(count).isEqualTo(3L);
		}
	}

	/**
	 * - 강의 생성자는 본인의 강의를 오픈할 수 있다.
	 * - 다른 생성자의 강의를 오픈할 수 없다.
	 */
	@Nested
	@DisplayName("강의 오픈")
	class Open {
		@DisplayName("강의 생성자는 본인의 강의를 오픈할 수 있다.")
		@Test
		void 강의_생성자는_본인의_강의를_오픈할_수_있다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			given(courseRepository.findById(1L)).willReturn(Optional.of(course));

			//when
			CourseInfo courseInfo = courseService.courseOpen(new CourseCommand.Open(1L, 1L));

			//then
			assertThat(courseInfo.status()).isEqualTo(CourseStatus.OPEN.name());
		}

		@DisplayName("다른 생성자의 강의를 오픈할 수 없다.")
		@Test
		void 다른_생성자의_강의를_오픈할_수_없다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			given(courseRepository.findById(1L)).willReturn(Optional.of(course));

			//then
			assertThatThrownBy(() -> courseService.courseOpen(new CourseCommand.Open(1L, 2L)))
					.isInstanceOf(com.liveklass.support.error.CoreException.class)
					.extracting("errorType")
					.isEqualTo(com.liveklass.support.error.ErrorType.FORBIDDEN);
		}
	}

	/**
	 * - 강의 생성자는 본인의 강의를 마감할 수 있다.
	 * - 다른 생성자의 강의를 마감할 수 없다.
	 */
	@Nested
	@DisplayName("강의 마감")
	class Close {
		@DisplayName("강의 생성자는 본인의 강의를 마감할 수 있다.")
		@Test
		void 강의_생성자는_본인의_강의를_마감할_수_있다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			course.open();
			given(courseRepository.findById(1L)).willReturn(Optional.of(course));

			//when
			CourseInfo courseInfo = courseService.courseClose(new CourseCommand.Close(1L, 1L));

			//then
			assertThat(courseInfo.status()).isEqualTo(CourseStatus.CLOSED.name());
		}

		@DisplayName("다른 생성자의 강의를 마감할 수 없다.")
		@Test
		void 다른_생성자의_강의를_마감할_수_없다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			given(courseRepository.findById(1L)).willReturn(Optional.of(course));

			//then
			assertThatThrownBy(() -> courseService.courseClose(new CourseCommand.Close(1L, 2L)))
					.isInstanceOf(com.liveklass.support.error.CoreException.class)
					.extracting("errorType")
					.isEqualTo(com.liveklass.support.error.ErrorType.FORBIDDEN);
		}
	}

	private CourseCommand.Create createCommand(Long creatorId, String title) {
		return new CourseCommand.Create(
				creatorId,
				title,
				"강의 설명",
				1000,
				30,
				LocalDate.now(),
				LocalDate.now().plusDays(30)
		);
	}

}
