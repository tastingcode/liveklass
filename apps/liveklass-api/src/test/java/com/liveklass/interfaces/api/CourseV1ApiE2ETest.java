package com.liveklass.interfaces.api;

import com.liveklass.domain.common.PageResponse;
import com.liveklass.domain.course.CourseCommand;
import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseRepository;
import com.liveklass.domain.course.CourseStatus;
import com.liveklass.domain.enrollment.EnrollmentCommand;
import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentRepository;
import com.liveklass.domain.user.UserCommand;
import com.liveklass.domain.user.UserEntity;
import com.liveklass.domain.user.UserRepository;
import com.liveklass.interfaces.api.course.CourseV1Dto;
import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CourseV1ApiE2ETest {

	private static final String ENDPOINT = "/api/v1/courses";

	private final TestRestTemplate testRestTemplate;
	private final DatabaseCleanUp databaseCleanUp;
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;

	@Autowired
	public CourseV1ApiE2ETest(
			TestRestTemplate testRestTemplate,
			DatabaseCleanUp databaseCleanUp,
			UserRepository userRepository,
			CourseRepository courseRepository,
			EnrollmentRepository enrollmentRepository
	) {
		this.testRestTemplate = testRestTemplate;
		this.databaseCleanUp = databaseCleanUp;
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.enrollmentRepository = enrollmentRepository;
	}

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	/**
	 * - 강의 등록이 성공할 경우, 생성된 강의 정보를 응답으로 반환한다.
	 * - CREATOR 역할이 아닌 사용자가 강의 등록 시도 시, `403 Forbidden` 응답을 반환한다.
	 */
	@DisplayName("POST /api/v1/courses")
	@Nested
	class Post {
		@DisplayName("강의 등록이 성공할 경우, 생성된 강의 정보를 응답으로 반환한다.")
		@Test
		public void 강의_등록이_성공할_경우_생성된_강의_정보를_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			CourseV1Dto.RegisterRequest request = registerRequest("자바 기초");
			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(creator.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT,
					HttpMethod.POST,
					new HttpEntity<>(request, headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().creatorId()).isEqualTo(creator.getId()),
					() -> assertThat(response.getBody().data().title()).isEqualTo(request.title()),
					() -> assertThat(response.getBody().data().status()).isEqualTo(CourseStatus.DRAFT.name())
			);
		}

		@DisplayName("CREATOR 역할이 아닌 사용자가 강의 등록 시도 시, `403 Forbidden` 응답을 반환한다.")
		@Test
		public void CREATOR_역할이_아닌_사용자가_강의_등록_시도_시_403_Forbidden_응답을_반환한다() {
			//given
			UserEntity student = saveUser("student1", "STUDENT");
			CourseV1Dto.RegisterRequest request = registerRequest("자바 기초");
			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(student.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT,
					HttpMethod.POST,
					new HttpEntity<>(request, headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is4xxClientError()),
					() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN)
			);
		}
	}

	/**
	 * - 상태값이 없으면 OPEN 상태의 강의 목록을 응답으로 반환한다.
	 * - 상태값이 주어지면 해당 상태의 강의 목록을 응답으로 반환한다.
	 */
	@DisplayName("GET /api/v1/courses")
	@Nested
	class GetCourses {
		@DisplayName("상태값이 없으면 OPEN 상태의 강의 목록을 응답으로 반환한다.")
		@Test
		public void 상태값이_없으면_OPEN_상태의_강의_목록을_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			CourseEntity draftCourse = CourseEntity.from(createCommand(creator.getId(), "자바 기초"));
			CourseEntity openCourse = CourseEntity.from(createCommand(creator.getId(), "스프링 기초"));
			openCourse.open();
			courseRepository.save(draftCourse);
			courseRepository.save(openCourse);

			ParameterizedTypeReference<ApiResponse<PageResponse<CourseV1Dto.CourseResponse>>> responseType = new ParameterizedTypeReference<>() {};

			//when
			ResponseEntity<ApiResponse<PageResponse<CourseV1Dto.CourseResponse>>> response = testRestTemplate.exchange(
					ENDPOINT,
					HttpMethod.GET,
					HttpEntity.EMPTY,
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().content()).hasSize(1),
					() -> assertThat(response.getBody().data().content().getFirst().title()).isEqualTo(openCourse.getTitle()),
					() -> assertThat(response.getBody().data().content().getFirst().status()).isEqualTo(CourseStatus.OPEN.name()),
					() -> assertThat(response.getBody().data().totalElements()).isEqualTo(1L),
					() -> assertThat(response.getBody().data().totalPage()).isEqualTo(1)
			);
		}

		@DisplayName("상태값이 주어지면 해당 상태의 강의 목록을 응답으로 반환한다.")
		@Test
		public void 상태값이_주어지면_해당_상태의_강의_목록을_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			CourseEntity draftCourse = CourseEntity.from(createCommand(creator.getId(), "자바 기초"));
			CourseEntity openCourse = CourseEntity.from(createCommand(creator.getId(), "스프링 기초"));
			openCourse.open();
			courseRepository.save(draftCourse);
			courseRepository.save(openCourse);

			ParameterizedTypeReference<ApiResponse<PageResponse<CourseV1Dto.CourseResponse>>> responseType = new ParameterizedTypeReference<>() {};

			//when
			ResponseEntity<ApiResponse<PageResponse<CourseV1Dto.CourseResponse>>> response = testRestTemplate.exchange(
					ENDPOINT + "?status=DRAFT",
					HttpMethod.GET,
					HttpEntity.EMPTY,
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().content()).hasSize(1),
					() -> assertThat(response.getBody().data().content().getFirst().title()).isEqualTo(draftCourse.getTitle()),
					() -> assertThat(response.getBody().data().content().getFirst().status()).isEqualTo(CourseStatus.DRAFT.name()),
					() -> assertThat(response.getBody().data().totalElements()).isEqualTo(1L),
					() -> assertThat(response.getBody().data().totalPage()).isEqualTo(1)
			);
		}
	}

	/**
	 * - 강의 상세 조회에 성공할 경우, 해당하는 강의 정보를 응답으로 반환한다.
	 * - 존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
	 */
	@DisplayName("GET /api/v1/courses/{courseId}")
	@Nested
	class GetCourse {
		@DisplayName("강의 상세 조회에 성공할 경우, 해당하는 강의 정보를 응답으로 반환한다.")
		@Test
		public void 강의_상세_조회에_성공할_경우_해당하는_강의_정보를_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			UserEntity student1 = saveUser("student1", "STUDENT");
			UserEntity student2 = saveUser("student2", "STUDENT");
			CourseEntity savedCourse = courseRepository.save(CourseEntity.from(createCommand(creator.getId(), "자바 기초")));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(savedCourse.getId(), student1.getId(), savedCourse.getCapacity())));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(savedCourse.getId(), student2.getId(), savedCourse.getCapacity())));

			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/" + savedCourse.getId(),
					HttpMethod.GET,
					HttpEntity.EMPTY,
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().id()).isEqualTo(savedCourse.getId()),
					() -> assertThat(response.getBody().data().title()).isEqualTo(savedCourse.getTitle()),
					() -> assertThat(response.getBody().data().status()).isEqualTo(savedCourse.getStatus().name()),
					() -> assertThat(response.getBody().data().applicants()).isEqualTo(2)
			);
		}

		@DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
		@Test
		public void 존재하지_않는_ID_로_조회할_경우_404_Not_Found_응답을_반환한다() {
			//given
			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/-1",
					HttpMethod.GET,
					HttpEntity.EMPTY,
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is4xxClientError()),
					() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
			);
		}
	}

	/**
	 * - 강의 오픈이 성공할 경우, OPEN 상태의 강의 정보를 응답으로 반환한다.
	 * - 다른 사용자의 강의를 오픈할 경우, `403 Forbidden` 응답을 반환한다.
	 */
	@DisplayName("PATCH /api/v1/courses/{courseId}/open")
	@Nested
	class PatchOpen {
		@DisplayName("강의 오픈이 성공할 경우, OPEN 상태의 강의 정보를 응답으로 반환한다.")
		@Test
		public void 강의_오픈이_성공할_경우_OPEN_상태의_강의_정보를_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			CourseEntity course = courseRepository.save(CourseEntity.from(createCommand(creator.getId(), "자바 기초")));

			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(creator.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/" + course.getId() + "/open",
					HttpMethod.PATCH,
					new HttpEntity<>(headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().id()).isEqualTo(course.getId()),
					() -> assertThat(response.getBody().data().status()).isEqualTo(CourseStatus.OPEN.name())
			);
		}

		@DisplayName("다른 사용자의 강의를 오픈할 경우, `403 Forbidden` 응답을 반환한다.")
		@Test
		public void 다른_사용자의_강의를_오픈할_경우_403_Forbidden_응답을_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			UserEntity otherCreator = saveUser("creator2", "CREATOR");
			CourseEntity course = courseRepository.save(CourseEntity.from(createCommand(creator.getId(), "자바 기초")));

			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(otherCreator.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/" + course.getId() + "/open",
					HttpMethod.PATCH,
					new HttpEntity<>(headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is4xxClientError()),
					() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN)
			);
		}
	}

	/**
	 * - 강의 마감이 성공할 경우, CLOSED 상태의 강의 정보를 응답으로 반환한다.
	 * - 다른 사용자의 강의를 마감할 경우, `403 Forbidden` 응답을 반환한다.
	 */
	@DisplayName("PATCH /api/v1/courses/{courseId}/close")
	@Nested
	class PatchClose {
		@DisplayName("강의 마감이 성공할 경우, CLOSED 상태의 강의 정보를 응답으로 반환한다.")
		@Test
		public void 강의_마감이_성공할_경우_CLOSED_상태의_강의_정보를_응답으로_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			CourseEntity course = CourseEntity.from(createCommand(creator.getId(), "자바 기초"));
			course.open();
			CourseEntity savedCourse = courseRepository.save(course);

			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(creator.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/" + savedCourse.getId() + "/close",
					HttpMethod.PATCH,
					new HttpEntity<>(headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().id()).isEqualTo(savedCourse.getId()),
					() -> assertThat(response.getBody().data().status()).isEqualTo(CourseStatus.CLOSED.name())
			);
		}

		@DisplayName("다른 사용자의 강의를 마감할 경우, `403 Forbidden` 응답을 반환한다.")
		@Test
		public void 다른_사용자의_강의를_마감할_경우_403_Forbidden_응답을_반환한다() {
			//given
			UserEntity creator = saveUser("creator1", "CREATOR");
			UserEntity otherCreator = saveUser("creator2", "CREATOR");
			CourseEntity course = CourseEntity.from(createCommand(creator.getId(), "자바 기초"));
			course.open();
			CourseEntity savedCourse = courseRepository.save(course);

			ParameterizedTypeReference<ApiResponse<CourseV1Dto.CourseResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(otherCreator.getId());

			//when
			ResponseEntity<ApiResponse<CourseV1Dto.CourseResponse>> response = testRestTemplate.exchange(
					ENDPOINT + "/" + savedCourse.getId() + "/close",
					HttpMethod.PATCH,
					new HttpEntity<>(headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is4xxClientError()),
					() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN)
			);
		}
	}

	private UserEntity saveUser(String loginId, String userRole) {
		UserCommand.Create command = new UserCommand.Create(loginId, userRole);
		return userRepository.save(UserEntity.from(command));
	}

	private CourseV1Dto.RegisterRequest registerRequest(String title) {
		return new CourseV1Dto.RegisterRequest(
				title,
				"강의 설명",
				1000,
				30,
				LocalDate.now(),
				LocalDate.now().plusDays(30)
		);
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

	private HttpHeaders headersWithUserId(Long userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("X-USER-ID", userId.toString());
		return headers;
	}
}
