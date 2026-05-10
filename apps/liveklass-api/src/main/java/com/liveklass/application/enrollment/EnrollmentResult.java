package com.liveklass.application.enrollment;

import com.liveklass.domain.enrollment.EnrollmentInfo;

import java.time.LocalDate;

public record EnrollmentResult(
		Long id,
		Long courseId,
		Long userId,
		String status,
		LocalDate confirmedDate
) {
	public static EnrollmentResult of(EnrollmentInfo enrollmentInfo) {
		return new EnrollmentResult(
				enrollmentInfo.id(),
				enrollmentInfo.courseId(),
				enrollmentInfo.userId(),
				enrollmentInfo.status(),
				enrollmentInfo.confirmedDate()
		);
	}
}
