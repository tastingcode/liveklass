package com.liveklass.domain.enrollment;

import com.liveklass.domain.BaseEntity;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "enrollment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentEntity extends BaseEntity {

	@Column(name = "course_id", nullable = false)
	private Long courseId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EnrollmentStatus status;

	@Column(nullable = true)
	private LocalDate confirmedDate;

	public static EnrollmentEntity from(EnrollmentCommand.Create command){
		EnrollmentEntity enrollment = new EnrollmentEntity();
		enrollment.courseId = command.courseId();
		enrollment.userId = command.userId();
		enrollment.status = EnrollmentStatus.PENDING;
		enrollment.guard();
		return enrollment;
	}

	@Override
	protected void guard() {
		if (courseId == null || courseId <= 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "강의 ID는 양수여야 합니다.");
		}
		if (userId == null || userId <= 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 양수여야 합니다.");
		}
		if (status == null) {
			throw new CoreException(ErrorType.BAD_REQUEST, "수강 신청 상태는 필수입니다.");
		}
	}

	public void confirm(){
		if (status != EnrollmentStatus.PENDING) {
			throw new CoreException(ErrorType.BAD_REQUEST, "결제 대기 상태의 신청만 확정할 수 있습니다.");
		}

		this.status = EnrollmentStatus.CONFIRMED;
		this.confirmedDate = LocalDate.now();
	}

	public void cancel(LocalDate cancelDate, int cancelPolicyDay) {
		if (status == EnrollmentStatus.CANCELLED) {
			throw new CoreException(ErrorType.BAD_REQUEST, "이미 취소된 수강 신청입니다.");
		}

		if (status == EnrollmentStatus.CONFIRMED && !isCancellableConfirmed(cancelDate, cancelPolicyDay)) {
			throw new CoreException(ErrorType.BAD_REQUEST, "결제 확정 후 취소 가능 기간이 지났습니다.");
		}
		this.status = EnrollmentStatus.CANCELLED;
	}

	private boolean isCancellableConfirmed(LocalDate cancelDate, int cancelPolicyDay) {
		return confirmedDate != null && !cancelDate.isAfter(confirmedDate.plusDays(cancelPolicyDay));
	}


}
