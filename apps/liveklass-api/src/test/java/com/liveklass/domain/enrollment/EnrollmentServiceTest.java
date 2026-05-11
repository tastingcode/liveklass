package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {
	@InjectMocks
	private EnrollmentService enrollmentService;

	@Mock
	private EnrollmentRepository enrollmentRepository;

	/**
	 * - 정원이 남아있으면 수강 신청 정보가 반환된다.
	 * - 강의 정원이 초과되면 CONFLICT 예외가 발생한다.
	 */
	@Nested
	@DisplayName("수강 신청 시")
	class Enroll {
		@DisplayName("정원이 남아있으면 수강 신청 정보가 반환된다.")
		@Test
		void 정원이_남아있으면_수강_신청_정보가_반환된다() {
			//given
			EnrollmentCommand.Create command = new EnrollmentCommand.Create(1L, 2L, 1);
			int applicants = 0;
			given(enrollmentRepository.save(any(EnrollmentEntity.class)))
					.willAnswer(invocation -> invocation.getArgument(0));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enroll(command, applicants);

			//then
			assertThat(enrollmentInfo.courseId()).isEqualTo(command.courseId());
			assertThat(enrollmentInfo.userId()).isEqualTo(command.userId());
			assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.PENDING.name());
		}

		@DisplayName("강의 정원이 초과되면 CONFLICT 예외가 발생한다.")
		@Test
		void 강의_정원이_초과되면_CONFLICT_예외가_발생한다() {
			//given
			EnrollmentCommand.Create command = new EnrollmentCommand.Create(1L, 2L, 1);
			int applicants = 1;

			//then
			assertThatThrownBy(() -> enrollmentService.enroll(command, applicants))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.CONFLICT);
		}
	}

	@DisplayName("수강 신청 인원 조회 시 결제 대기와 결제 확정 상태만 집계한다.")
	@Test
	void 수강_신청_인원_조회_시_결제_대기와_결제_확정_상태만_집계한다() {
		//given
		Long courseId = 1L;
		List<EnrollmentStatus> applicantStatuses = List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED);
		given(enrollmentRepository.countByCourseIdAndStatusIn(courseId, applicantStatuses))
				.willReturn(2);

		//when
		int applicants = enrollmentService.getApplicantsCount(courseId);

		//then
		assertThat(applicants).isEqualTo(2);
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
		void 결제_대기_상태의_수강_신청은_결제_확정_처리된다() {
			//given
			EnrollmentCommand.Confirm command = new EnrollmentCommand.Confirm(1L, 2L);
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(command.courseId(), command.userId(), 30));
			given(enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId()))
					.willReturn(Optional.of(enrollment));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentConfirm(command);

			//then
			assertThat(enrollmentInfo.courseId()).isEqualTo(command.courseId());
			assertThat(enrollmentInfo.userId()).isEqualTo(command.userId());
			assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.CONFIRMED.name());
			assertThat(enrollmentInfo.confirmedDate()).isNotNull();
		}

		@DisplayName("수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.")
		@Test
		void 수강_신청을_찾을_수_없으면_NOT_FOUND_예외가_발생한다() {
			//given
			EnrollmentCommand.Confirm command = new EnrollmentCommand.Confirm(1L, 2L);
			given(enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId()))
					.willReturn(Optional.empty());

			//then
			assertThatThrownBy(() -> enrollmentService.enrollmentConfirm(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.NOT_FOUND);
		}
	}

	/**
	 * - 수강 신청은 취소 처리된다.
	 * - 수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.
	 */
	@Nested
	@DisplayName("수강 신청 취소 시")
	class Cancel {
		@DisplayName("수강 신청은 취소 처리된다.")
		@Test
		void 수강_신청은_취소_처리된다() {
			//given
			EnrollmentCommand.Cancel command = new EnrollmentCommand.Cancel(1L, 2L);
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(command.courseId(), command.userId(), 30));
			given(enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId()))
					.willReturn(Optional.of(enrollment));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentCancel(command);

			//then
			assertThat(enrollmentInfo.courseId()).isEqualTo(command.courseId());
			assertThat(enrollmentInfo.userId()).isEqualTo(command.userId());
			assertThat(enrollmentInfo.status()).isEqualTo(EnrollmentStatus.CANCELLED.name());
		}

		@DisplayName("수강 신청을 찾을 수 없으면 NOT_FOUND 예외가 발생한다.")
		@Test
		void 수강_신청을_찾을_수_없으면_NOT_FOUND_예외가_발생한다() {
			//given
			EnrollmentCommand.Cancel command = new EnrollmentCommand.Cancel(1L, 2L);
			given(enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId()))
					.willReturn(Optional.empty());

			//then
			assertThatThrownBy(() -> enrollmentService.enrollmentCancel(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.NOT_FOUND);
		}
	}

	/**
	 * - 사용자 ID에 해당하는 수강 신청 목록이 반환된다.
	 */
	@DisplayName("내 수강 신청 목록 조회 시 사용자 ID에 해당하는 수강 신청 목록이 반환된다.")
	@Test
	void 내_수강_신청_목록_조회_시_사용자_ID에_해당하는_수강_신청_목록이_반환된다() {
		//given
		EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L, 30));
		given(enrollmentRepository.findAllByUserId(2L)).willReturn(List.of(enrollment));

		//when
		List<EnrollmentInfo> enrollments = enrollmentService.findMyEnrollments(new EnrollmentCommand.FindMy(2L));

		//then
		assertThat(enrollments).hasSize(1);
		assertThat(enrollments.getFirst().userId()).isEqualTo(2L);
	}
}
