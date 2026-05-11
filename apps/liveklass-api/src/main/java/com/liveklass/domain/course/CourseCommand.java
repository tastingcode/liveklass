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

	public record Open(Long courseId, Long creatorId) {
	}

	public record Close(Long courseId, Long creatorId) {
	}

	public record Search(String status, int page, int size) {
	}

}
