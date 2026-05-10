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
	 * - 상태 필터가 주어지면 해당 상태의 강의 목록이 반환된다.
	 * - 상태 필터가 없으면 전체 강의 목록이 반환된다.
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
			Optional<CourseInfo> courseInfo = courseService.findCourse(new CourseCommand.Find(1L));

			//then
			assertThat(courseInfo).isPresent();
			assertThat(courseInfo.get().title()).isEqualTo(course.getTitle());
		}

		@DisplayName("상태 필터가 주어지면 해당 상태의 강의 목록이 반환된다.")
		@Test
		void 상태_필터가_주어지면_해당_상태의_강의_목록이_반환된다() {
			//given
			CourseEntity openCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			openCourse.open();
			given(courseRepository.findAllByStatus(CourseStatus.OPEN)).willReturn(List.of(openCourse));

			//when
			List<CourseInfo> courses = courseService.findCourses(new CourseCommand.Search("OPEN"));

			//then
			assertThat(courses).hasSize(1);
			assertThat(courses.getFirst().status()).isEqualTo(CourseStatus.OPEN.name());
		}

		@DisplayName("상태 필터가 없으면 전체 강의 목록이 반환된다.")
		@Test
		void 상태_필터가_없으면_전체_강의_목록이_반환된다() {
			//given
			CourseEntity draftCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			CourseEntity openCourse = CourseEntity.from(createCommand(1L, "스프링 기초"));
			openCourse.open();
			given(courseRepository.findAll()).willReturn(List.of(draftCourse, openCourse));

			//when
			List<CourseInfo> courses = courseService.findCourses(new CourseCommand.Search(null));

			//then
			assertThat(courses).hasSize(2);
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
