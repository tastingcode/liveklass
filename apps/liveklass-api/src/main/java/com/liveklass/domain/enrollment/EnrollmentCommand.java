package com.liveklass.domain.enrollment;

public class EnrollmentCommand {
	public record Create(
			Long courseId,
			Long userId
	) {
	}
}

