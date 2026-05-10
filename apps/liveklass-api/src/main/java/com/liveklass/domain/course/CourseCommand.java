package com.liveklass.domain.course;

import java.time.LocalDate;

public class CourseCommand {
	public record Create(
			Long creatorId,
			String title,
			String description,
			int price,
			int capacity,
			LocalDate startDate,
			LocalDate endDate
	) {
	}
}
