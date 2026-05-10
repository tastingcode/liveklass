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
			given(enrollmentRepository.countByCourseIdAndStatusNot(command.courseId(), EnrollmentStatus.CANCELLED))
					.willReturn(0L);
			given(enrollmentRepository.save(any(EnrollmentEntity.class)))
					.willAnswer(invocation -> invocation.getArgument(0));

			//when
			EnrollmentInfo enrollmentInfo = enrollmentService.enroll(command);

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
			given(enrollmentRepository.countByCourseIdAndStatusNot(command.courseId(), EnrollmentStatus.CANCELLED))
					.willReturn(1L);

			//then
			assertThatThrownBy(() -> enrollmentService.enroll(command))
					.isInstanceOf(CoreException.class)
					.extracting("errorType")
					.isEqualTo(ErrorType.CONFLICT);
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
