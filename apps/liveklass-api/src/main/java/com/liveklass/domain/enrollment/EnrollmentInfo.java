package com.liveklass.domain.enrollment;

import java.time.LocalDate;

public record EnrollmentInfo(
		Long id,
		Long courseId,
		Long userId,
		String status,
		LocalDate confirmedDate
) {
	public static EnrollmentInfo from(EnrollmentEntity enrollment) {
		return new EnrollmentInfo(
				enrollment.getId(),
				enrollment.getCourseId(),
				enrollment.getUserId(),
				enrollment.getStatus().name(),
				enrollment.getConfirmedDate()
		);
	}
}
