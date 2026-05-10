package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrollmentEntityTest {

	/**
	 * - 수강 신청 생성 시 PENDING으로 생성된다.
	 * - 수강 신청 생성 시 강의 ID가 양수가 아니면 실패한다.
	 */
	@Nested
	@DisplayName("수강 신청 생성")
	class Create {
		@DisplayName("수강 신청 생성 시 PENDING으로 생성된다.")
		@Test
		public void 수강_신청_생성_시_PENDING으로_생성된다() {
			//given
			Long courseId = 1L;
			Long userId = 2L;
			EnrollmentCommand.Create command = new EnrollmentCommand.Create(courseId, userId);

			//when
			EnrollmentEntity enrollment = EnrollmentEntity.from(command);

			//then
			assertThat(enrollment.getCourseId()).isEqualTo(courseId);
			assertThat(enrollment.getUserId()).isEqualTo(userId);
			assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
		}

		@DisplayName("수강 신청 생성 시 강의 ID가 양수가 아니면 실패한다.")
		@Test
		public void 수강_신청_생성_시_강의_ID가_양수가_아니면_실패한다() {
			//given
			EnrollmentCommand.Create command = new EnrollmentCommand.Create(0L, 2L);

			//then
			CoreException coreException = assertThrows(CoreException.class, () -> {
				EnrollmentEntity.from(command);
			});

			assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
		}

	}

	/**
	 * - PENDING 상태의 수강 신청은 CONFIRMED로 변경된다.
	 * - PENDING 상태가 아닌 수강 신청은 확정할 수 없다.
	 */
	@Nested
	@DisplayName("수강 신청 확정")
	class Confirm {
		@DisplayName("PENDING 상태의 수강 신청은 CONFIRMED로 변경된다.")
		@Test
		public void PENDING_상태의_수강_신청은_CONFIRMED로_변경된다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));

			//when
			enrollment.confirm();

			//then
			assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);
		}

		@DisplayName("수강 신청 확정 시 확정일이 저장된다.")
		@Test
		public void 수강_신청_확정_시_확정일이_저장된다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));

			//when
			enrollment.confirm();

			//then
			assertThat(enrollment.getConfirmedDate()).isEqualTo(LocalDate.now());
		}

		@DisplayName("PENDING 상태가 아닌 수강 신청은 확정할 수 없다.")
		@Test
		public void PENDING_상태가_아닌_수강_신청은_확정할_수_없다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));
			enrollment.confirm();

			//then
			CoreException coreException = assertThrows(CoreException.class, () -> {
				enrollment.confirm();
			});

			assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
		}
	}

	/**
	 * - PENDING 상태의 수강 신청은 CANCELLED로 변경된다.
	 * - 이미 취소된 수강 신청은 다시 취소할 수 없다.
	 * - CONFIRMED 상태의 수강 신청은 취소 가능 기간 내에 CANCELLED로 변경된다.
	 * - CONFIRMED 상태의 수강 신청은 취소 가능 기간이 지나면 취소할 수 없다.
	 */
	@Nested
	@DisplayName("수강 신청 취소")
	class Cancel {
		@DisplayName("PENDING 상태의 수강 신청 취소시 CANCELLED로 변경된다.")
		@Test
		public void PENDING_상태의_수강_신청_취소시_CANCELLED로_변경된다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));
			LocalDate cancelDate = LocalDate.now();
			//when
			enrollment.cancel(cancelDate, 7);

			//then
			assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
		}

		@DisplayName("이미 취소된 수강 신청은 다시 취소할 수 없다.")
		@Test
		public void 이미_취소된_수강_신청은_다시_취소할_수_없다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));
			LocalDate cancelDate = LocalDate.now();
			enrollment.cancel(cancelDate, 7);

			//then
			CoreException coreException = assertThrows(CoreException.class, () -> {
				enrollment.cancel(cancelDate, 7);
			});

			assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
		}

		@DisplayName("CONFIRMED 상태의 수강 신청은 취소 가능 기간 내에 CANCELLED로 변경된다.")
		@Test
		public void CONFIRMED_상태의_수강_신청은_취소_가능_기간_내에_CANCELLED로_변경된다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));
			enrollment.confirm();
			LocalDate confirmedDate = enrollment.getConfirmedDate();

			//when
			enrollment.cancel(confirmedDate.plusDays(7), 7);

			//then
			assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
		}

		@DisplayName("CONFIRMED 상태의 수강 신청은 취소 가능 기간이 지나면 취소할 수 없다.")
		@Test
		public void CONFIRMED_상태의_수강_신청은_취소_가능_기간이_지나면_취소할_수_없다() {
			//given
			EnrollmentEntity enrollment = EnrollmentEntity.from(new EnrollmentCommand.Create(1L, 2L));
			enrollment.confirm();
			LocalDate confirmedDate = enrollment.getConfirmedDate();

			//then
			CoreException coreException = assertThrows(CoreException.class, () -> {
				enrollment.cancel(confirmedDate.plusDays(8), 7);
			});

			assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
			assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);
		}
	}
}
