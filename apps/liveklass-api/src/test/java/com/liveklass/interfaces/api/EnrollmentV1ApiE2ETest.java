package com.liveklass.interfaces.api;

import com.liveklass.domain.course.CourseCommand;
import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseRepository;
import com.liveklass.domain.enrollment.EnrollmentCommand;
import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentRepository;
import com.liveklass.domain.enrollment.EnrollmentStatus;
import com.liveklass.domain.user.UserCommand;
import com.liveklass.domain.user.UserEntity;
import com.liveklass.domain.user.UserRepository;
import com.liveklass.interfaces.api.enrollment.EnrollmentV1Dto;
import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EnrollmentV1ApiE2ETest {
	private static final String ENDPOINT = "/api/v1/enrollments";

	private final TestRestTemplate testRestTemplate;
	private final DatabaseCleanUp databaseCleanUp;
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;

	@Autowired
	public EnrollmentV1ApiE2ETest(
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
	 * - 수강 신청이 성공할 경우, 생성된 수강 신청 정보를 응답으로 반환한다.
	 * - 정원이 초과된 강의에 수강 신청할 경우, `409 Conflict` 응답을 반환한다.
	 */
	@DisplayName("POST /api/v1/enrollments")
	@Nested
	class Post {
		@DisplayName("수강 신청이 성공할 경우, 생성된 수강 신청 정보를 응답으로 반환한다.")
		@Test
		public void 수강_신청이_성공할_경우_생성된_수강_신청_정보를_응답으로_반환한다() {
			//given
			UserEntity student = saveUser("student1", "STUDENT");
			CourseEntity course = courseRepository.save(openCourse(30, "자바 기초"));
			EnrollmentV1Dto.EnrollRequest request = new EnrollmentV1Dto.EnrollRequest(course.getId());
			ParameterizedTypeReference<ApiResponse<EnrollmentV1Dto.EnrollmentResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(student.getId());

			//when
			ResponseEntity<ApiResponse<EnrollmentV1Dto.EnrollmentResponse>> response = testRestTemplate.exchange(
					ENDPOINT,
					HttpMethod.POST,
					new HttpEntity<>(request, headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data().courseId()).isEqualTo(course.getId()),
					() -> assertThat(response.getBody().data().userId()).isEqualTo(student.getId()),
					() -> assertThat(response.getBody().data().status()).isEqualTo(EnrollmentStatus.PENDING.name())
			);
		}

		@DisplayName("정원이 초과된 강의에 수강 신청할 경우, `409 Conflict` 응답을 반환한다.")
		@Test
		public void 정원이_초과된_강의에_수강_신청할_경우_409_Conflict_응답을_반환한다() {
			//given
			UserEntity student1 = saveUser("student1", "STUDENT");
			UserEntity student2 = saveUser("student2", "STUDENT");
			CourseEntity course = courseRepository.save(openCourse(1, "자바 기초"));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(course.getId(), student1.getId(), course.getCapacity())));

			EnrollmentV1Dto.EnrollRequest request = new EnrollmentV1Dto.EnrollRequest(course.getId());
			ParameterizedTypeReference<ApiResponse<EnrollmentV1Dto.EnrollmentResponse>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(student2.getId());

			//when
			ResponseEntity<ApiResponse<EnrollmentV1Dto.EnrollmentResponse>> response = testRestTemplate.exchange(
					ENDPOINT,
					HttpMethod.POST,
					new HttpEntity<>(request, headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is4xxClientError()),
					() -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT)
			);
		}
	}

	/**
	 * - 내 수강 신청 목록 조회에 성공할 경우, 내 수강 신청 목록을 응답으로 반환한다.
	 */
	@DisplayName("GET /api/v1/enrollments/me")
	@Nested
	class GetMyEnrollments {
		@DisplayName("내 수강 신청 목록 조회에 성공할 경우, 내 수강 신청 목록을 응답으로 반환한다.")
		@Test
		public void 내_수강_신청_목록_조회에_성공할_경우_내_수강_신청_목록을_응답으로_반환한다() {
			//given
			UserEntity student = saveUser("student1", "STUDENT");
			UserEntity otherStudent = saveUser("student2", "STUDENT");
			CourseEntity course1 = courseRepository.save(openCourse(30, "자바 기초"));
			CourseEntity course2 = courseRepository.save(openCourse(30, "스프링 기초"));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(course1.getId(), student.getId(), course1.getCapacity())));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(course2.getId(), student.getId(), course2.getCapacity())));
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(course1.getId(), otherStudent.getId(), course1.getCapacity())));

			ParameterizedTypeReference<ApiResponse<List<EnrollmentV1Dto.EnrollmentResponse>>> responseType = new ParameterizedTypeReference<>() {};
			HttpHeaders headers = headersWithUserId(student.getId());

			//when
			ResponseEntity<ApiResponse<List<EnrollmentV1Dto.EnrollmentResponse>>> response = testRestTemplate.exchange(
					ENDPOINT + "/me",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					responseType
			);

			//then
			Assertions.assertAll(
					() -> assertTrue(response.getStatusCode().is2xxSuccessful()),
					() -> assertThat(response.getBody().data()).hasSize(2),
					() -> assertThat(response.getBody().data())
							.extracting(EnrollmentV1Dto.EnrollmentResponse::userId)
							.containsOnly(student.getId())
			);
		}
	}

	private UserEntity saveUser(String loginId, String userRole) {
		UserCommand.Create command = new UserCommand.Create(loginId, userRole);
		return userRepository.save(UserEntity.from(command));
	}

	private CourseEntity openCourse(int capacity, String title) {
		CourseEntity course = CourseEntity.from(new CourseCommand.Create(
				1L,
				title,
				"강의 설명",
				1000,
				capacity,
				LocalDate.now(),
				LocalDate.now().plusDays(30)
		));
		course.open();
		return course;
	}

	private HttpHeaders headersWithUserId(Long userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("X-USER-ID", userId.toString());
		return headers;
	}
}
