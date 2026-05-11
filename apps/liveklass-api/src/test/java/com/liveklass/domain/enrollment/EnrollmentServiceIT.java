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
			EnrollmentInfo enrollmentInfo = enrollmentService.enroll(command, 0);

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
			assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCommand.Create(courseId, 3L, 1), 1))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.CONFLICT);
		}

		@DisplayName("취소된 수강 신청은 정원 계산에서 제외된다.")
		@Test
		public void 취소된_수강_신청은_정원_계산에서_제외된다() {
			//given
			Long courseId = 1L;
			EnrollmentEntity cancelledEnrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, 2L, 1));
			cancelledEnrollment.cancel(java.time.LocalDate.now(), 7);
			enrollmentRepository.save(cancelledEnrollment);
			int applicants = enrollmentService.getApplicantsCount(courseId);

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enroll(new EnrollmentCommand.Create(courseId, 3L, 1), applicants);

			//then
			assertAll(
					() -> assertThat(enrollmentInfo.courseId()).isEqualTo(courseId),
					() -> assertThat(enrollmentInfo.userId()).isEqualTo(3L),
					() -> assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.PENDING.name()),
					() -> assertThat(applicants).isZero()
			);
		}
	}

	/**
	 * - 결제 대기 상태의 수강 신청은 결제 확정 처리된다.
	 * - 수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.
	 */
	@Nested
	@DisplayName("수강 신청 결제 확정 시")
	class Confirm {
		@DisplayName("결제 대기 상태의 수강 신청은 결제 확정 처리된다.")
		@Test
		public void 결제_대기_상태의_수강_신청은_결제_확정_처리된다() {
			//given
			Long courseId = 1L;
			Long userId = 2L;
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, userId, 30)));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentConfirm(new EnrollmentCommand.Confirm(courseId, userId));

			//then
			assertAll(
					() -> assertThat(enrollmentInfo.courseId()).isEqualTo(courseId),
					() -> assertThat(enrollmentInfo.userId()).isEqualTo(userId),
					() -> assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.CONFIRMED.name()),
					() -> assertThat(enrollmentInfo.confirmedDate()).isNotNull(),
					() -> assertThat(enrollmentRepository.findByCourseIdAndUserId(courseId, userId).orElseThrow().getStatus())
							.isEqualTo(EnrollmentStatus.CONFIRMED)
			);
		}

		@DisplayName("수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.")
		@Test
		public void 수강_신청을_찾을_수_없으면_NOT_FOUND_예외가_발생한다() {
			//given
			EnrollmentCommand.Confirm command = new EnrollmentCommand.Confirm(1L, 2L);

			//then
			assertThatThrownBy(() -> enrollmentService.enrollmentConfirm(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.NOT_FOUND);
		}
	}

	/**
	 * - 수강 신청 취소 시 수강 신청 상태가 취소된다.
	 * - 수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.
	 */
	@Nested
	@DisplayName("수강 신청 취소 시")
	class Cancel {
		@DisplayName("수강 신청 취소 시 수강 신청 상태가 취소된다.")
		@Test
		public void 수강_신청_취소_시_수강_신청_상태가_취소된다() {
			//given
			Long courseId = 1L;
			Long userId = 2L;
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, userId, 30)));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentCancel(new EnrollmentCommand.Cancel(courseId, userId));

			//then
			assertAll(
					() -> assertThat(enrollmentInfo.courseId()).isEqualTo(courseId),
					() -> assertThat(enrollmentInfo.userId()).isEqualTo(userId),
					() -> assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.CANCELLED.name()),
					() -> assertThat(enrollmentRepository.findByCourseIdAndUserId(courseId, userId).orElseThrow().getStatus())
							.isEqualTo(EnrollmentStatus.CANCELLED)
			);
		}

		@DisplayName("수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.")
		@Test
		public void 수강_신청을_찾을_수_없으면_NOT_FOUND_예외가_발생한다() {
			//given
			EnrollmentCommand.Cancel command = new EnrollmentCommand.Cancel(1L, 2L);

			//then
			assertThatThrownBy(() -> enrollmentService.enrollmentCancel(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.NOT_FOUND);
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

	@DisplayName("수강 신청 인원 조회 시 PENDING과 CONFIRMED 상태만 집계한다.")
	@Test
	public void 수강_신청_인원_조회_시_PENDING과_CONFIRMED_상태만_집계한다() {
		//given
		Long courseId = 1L;
		EnrollmentEntity pendingEnrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, 2L, 30));
		EnrollmentEntity confirmedEnrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, 3L, 30));
		confirmedEnrollment.confirm();
		EnrollmentEntity cancelledEnrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(courseId, 4L, 30));
		cancelledEnrollment.cancel(java.time.LocalDate.now(), 7);

		enrollmentRepository.save(pendingEnrollment);
		enrollmentRepository.save(confirmedEnrollment);
		enrollmentRepository.save(cancelledEnrollment);

		//when
		int enrollmentCount = enrollmentService.getApplicantsCount(courseId);

		//then
		assertThat(enrollmentCount).isEqualTo(2);
	}
}
