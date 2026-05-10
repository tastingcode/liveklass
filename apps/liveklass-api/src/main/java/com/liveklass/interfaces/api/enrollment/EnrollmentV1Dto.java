package com.liveklass.interfaces.api.enrollment;

import com.liveklass.application.enrollment.EnrollmentCriteria;
import com.liveklass.application.enrollment.EnrollmentResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class EnrollmentV1Dto {
	public record EnrollRequest(
			@NotNull(message = "강의 ID는 필수입니다.")
			@Positive(message = "강의 ID는 양수여야 합니다.")
			Long courseId
	) {
		public EnrollmentCriteria.Enroll toCriteria(Long userId) {
			return new EnrollmentCriteria.Enroll(courseId, userId);
		}
	}

	public record EnrollmentResponse(
			Long id,
			Long courseId,
			Long userId,
			String status,
			LocalDate confirmedDate
	) {
		public static EnrollmentResponse from(EnrollmentResult enrollmentResult) {
			return new EnrollmentResponse(
					enrollmentResult.id(),
					enrollmentResult.courseId(),
					enrollmentResult.userId(),
					enrollmentResult.status(),
					enrollmentResult.confirmedDate()
			);
		}
	}
}
