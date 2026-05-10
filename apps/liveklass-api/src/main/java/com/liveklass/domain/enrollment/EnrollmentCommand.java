package com.liveklass.domain.enrollment;

public class EnrollmentCommand {
	public record Create(
			Long courseId,
			Long userId,
			int courseCapacity
	) {
	}

	public record FindMy(Long userId) {
	}
}
