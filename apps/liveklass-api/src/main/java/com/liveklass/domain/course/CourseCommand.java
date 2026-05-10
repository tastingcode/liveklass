package com.liveklass.domain.course;

import java.time.LocalDate;

public class CourseCommand {
	public record Create(
			Long userId,
			String title,
			String description,
			int price,
			int capacity,
			LocalDate startDate,
			LocalDate endDate
	) {
	}

	public record Find(Long courseId) {
	}

	public record Search(String status) {
		public CourseStatus toCourseStatus() {
			if (status == null || status.isBlank()) {
				return null;
			}

			return CourseStatus.from(status);
		}
	}
}
