package com.liveklass.domain.course;

import java.time.LocalDate;

public record CourseInfo(
		Long id,
		Long creatorId,
		String title,
		String description,
		String status,
		int price,
		int capacity,
		LocalDate startDate,
		LocalDate endDate
) {
	public static CourseInfo from(CourseEntity course) {
		return new CourseInfo(
				course.getId(),
				course.getCreatorId(),
				course.getTitle(),
				course.getDescription(),
				course.getStatus().name(),
				course.getPrice(),
				course.getCapacity(),
				course.getStartDate(),
				course.getEndDate()
		);
	}
}
