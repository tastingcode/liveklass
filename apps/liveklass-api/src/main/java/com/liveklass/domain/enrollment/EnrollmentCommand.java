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

	public record FindCourseStudents(Long courseId, int page, int size) {
	}

	public record Confirm(Long courseId, Long userId) {
	}

	public record Cancel(Long courseId, Long userId) {
	}
}
