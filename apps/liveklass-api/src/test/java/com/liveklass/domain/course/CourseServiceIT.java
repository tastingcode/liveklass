package com.liveklass.domain.course;

import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class CourseServiceIT {

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private DatabaseCleanUp databaseCleanUp;

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	/**
	 * - 강의 생성 시 강의 정보가 반환된다.
	 */
	@Nested
	@DisplayName("강의 생성 시")
	class Create {
		@DisplayName("강의 생성 시 강의 정보가 반환된다.")
		@Test
		public void 강의_생성_시_강의_정보가_반환된다() {
			//given
			CourseCommand.Create command = createCommand(1L, "자바 기초");

			//when
			CourseInfo courseInfo = courseService.create(command);

			//then
			assertAll(
					() -> assertThat(courseInfo.id()).isNotNull(),
					() -> assertThat(courseInfo.creatorId()).isEqualTo(command.userId()),
					() -> assertThat(courseInfo.title()).isEqualTo(command.title()),
					() -> assertThat(courseInfo.description()).isEqualTo(command.description()),
					() -> assertThat(courseInfo.status()).isEqualTo(CourseStatus.DRAFT.name()),
					() -> assertThat(courseInfo.price()).isEqualTo(command.price()),
					() -> assertThat(courseInfo.capacity()).isEqualTo(command.capacity()),
					() -> assertThat(courseInfo.startDate()).isEqualTo(command.startDate()),
					() -> assertThat(courseInfo.endDate()).isEqualTo(command.endDate())
			);
		}
	}

	/**
	 * - 해당 ID의 강의가 존재할 경우 강의 정보가 반환된다.
	 * - 해당 ID의 강의가 존재하지 않을 경우, 빈 Optional 이 반환된다.
	 */
	@Nested
	@DisplayName("강의 단건 조회 시")
	class FindCourse {
		@DisplayName("해당 ID의 강의가 존재할 경우 강의 정보가 반환된다.")
		@Test
		public void 해당_ID의_강의가_존재할_경우_강의_정보가_반환된다() {
			//given
			CourseEntity course = CourseEntity.from(createCommand(1L, "자바 기초"));
			CourseEntity savedCourse = courseRepository.save(course);
			CourseCommand.Find command = new CourseCommand.Find(savedCourse.getId());

			//when
			Optional<CourseInfo> courseInfo = courseService.findCourseInfo(command);

			//then
			assertAll(
					() -> assertThat(courseInfo).isPresent(),
					() -> assertThat(courseInfo.get().id()).isEqualTo(savedCourse.getId()),
					() -> assertThat(courseInfo.get().title()).isEqualTo(savedCourse.getTitle()),
					() -> assertThat(courseInfo.get().status()).isEqualTo(savedCourse.getStatus().name())
			);
		}

		@DisplayName("해당 ID의 강의가 존재하지 않을 경우, 빈 Optional 이 반환된다.")
		@Test
		public void 해당_ID의_강의가_존재하지_않을_경우_빈_Optional_이_반환된다() {
			//given
			CourseCommand.Find command = new CourseCommand.Find(-1L);

			//when
			Optional<CourseInfo> courseInfo = courseService.findCourseInfo(command);

			//then
			assertThat(courseInfo).isEmpty();
		}
	}

	/**
	 * - 상태값이 없으면 OPEN 상태의 강의 목록과 강의 수가 반환된다.
	 * - 상태값이 주어지면 해당 상태의 강의 목록과 강의 수가 반환된다.
	 */
	@Nested
	@DisplayName("강의 목록 조회 시")
	class FindCourses {
		@DisplayName("상태값이 없으면 OPEN 상태의 강의 목록과 강의 수가 반환된다.")
		@Test
		public void 상태값이_없으면_OPEN_상태의_강의_목록과_강의_수가_반환된다() {
			//given
			CourseEntity draftCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			CourseEntity openCourse = CourseEntity.from(createCommand(1L, "스프링 기초"));
			openCourse.open();
			courseRepository.save(draftCourse);
			courseRepository.save(openCourse);
			CourseCommand.Search command = new CourseCommand.Search("OPEN", 0, 10);

			//when
			List<CourseInfo> courses = courseService.findCourses(command);
			long count = courseService.countCourses(command);

			//then
			assertAll(
					() -> assertThat(courses).hasSize(1),
					() -> assertThat(courses.getFirst().title()).isEqualTo(openCourse.getTitle()),
					() -> assertThat(courses.getFirst().status()).isEqualTo(CourseStatus.OPEN.name()),
					() -> assertThat(count).isEqualTo(1L)
			);
		}

		@DisplayName("상태값이 주어지면 해당 상태의 강의 목록과 강의 수가 반환된다.")
		@Test
		public void 상태값이_주어지면_해당_상태의_강의_목록과_강의_수가_반환된다() {
			//given
			CourseEntity draftCourse = CourseEntity.from(createCommand(1L, "자바 기초"));
			CourseEntity openCourse = CourseEntity.from(createCommand(1L, "스프링 기초"));
			openCourse.open();
			courseRepository.save(draftCourse);
			courseRepository.save(openCourse);
			CourseCommand.Search command = new CourseCommand.Search("DRAFT", 0, 10);

			//when
			List<CourseInfo> courses = courseService.findCourses(command);
			long count = courseService.countCourses(command);

			//then
			assertAll(
					() -> assertThat(courses).hasSize(1),
					() -> assertThat(courses.getFirst().title()).isEqualTo(draftCourse.getTitle()),
					() -> assertThat(courses.getFirst().status()).isEqualTo(CourseStatus.DRAFT.name()),
					() -> assertThat(count).isEqualTo(1L)
			);
		}
	}

	/**
	 * - 강의 생성자는 본인의 강의를 오픈할 수 있다.
	 */
	@Nested
	@DisplayName("강의 오픈 시")
	class Open {
		@DisplayName("강의 생성자는 본인의 강의를 오픈할 수 있다.")
		@Test
		public void 강의_생성자는_본인의_강의를_오픈할_수_있다() {
			//given
			CourseEntity course = courseRepository.save(CourseEntity.from(createCommand(1L, "자바 기초")));

			//when
			CourseInfo courseInfo = courseService.courseOpen(new CourseCommand.Open(course.getId(), course.getCreatorId()));

			//then
			assertAll(
					() -> assertThat(courseInfo.id()).isEqualTo(course.getId()),
					() -> assertThat(courseInfo.status()).isEqualTo(CourseStatus.OPEN.name())
			);
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
