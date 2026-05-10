package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class EnrollmentServiceIT {
	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private DatabaseCleanUp databaseCleanUp;

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	/**
	 * - 수강 신청 시 수강 신청 정보가 반환된다.
	 * - 정원이 초과된 강의에 수강 신청할 경우 실패한다.
	 */
	@Nested
	@DisplayName("수강 신청 시")
	class Enroll {
		@DisplayName("수강 신청 시 수강 신청 정보가 반환된다.")
		@Test
		public void 수강_신청_시_수강_신청_정보가_반환된다() {
			//given
			EnrollmentCommand.Create command = new EnrollmentCommand.Create(1L, 2L, 30);

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enroll(command);

			//then
			assertAll(
					() -> assertThat(enrollmentInfo.id()).isNotNull(),
					() -> assertThat(enrollmentInfo.courseId()).isEqualTo(command.courseId()),
					() -> assertThat(enrollmentInfo.userId()).isEqualTo(command.userId()),
					() -> assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.PENDING.name())
			);
		}

		@DisplayName("정원이 초과된 강의에 수강 신청할 경우 실패한다.")
		@Test
		public void 정원이_초과된_강의에_수강_신청할_경우_실패한다() {
			//given
			Long courseId = 1L;
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, 2L, 1)));

			//then
			assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCommand.Create(courseId, 3L, 1)))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.CONFLICT);
		}
	}

	/**
	 * - 내 수강 신청 목록 조회 시 사용자 ID에 해당하는 수강 신청 목록이 반환된다.
	 */
	@DisplayName("내 수강 신청 목록 조회 시 사용자 ID에 해당하는 수강 신청 목록이 반환된다.")
	@Test
	public void 내_수강_신청_목록_조회_시_사용자_ID에_해당하는_수강_신청_목록이_반환된다() {
		//given
		enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L, 30)));
		enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(2L, 2L, 30)));
		enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 3L, 30)));

		//when
		List<EnrollmentInfo> enrollments = enrollmentService.findMyEnrollments(new EnrollmentCommand.FindMy(2L));

		//then
		assertAll(
				() -> assertThat(enrollments).hasSize(2),
				() -> assertThat(enrollments)
						.extracting(EnrollmentInfo::userId)
						.containsOnly(2L)
		);
	}
}
