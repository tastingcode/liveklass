package com.liveklass.application.course;

import com.liveklass.domain.course.CourseInfo;

import java.time.LocalDate;

public record CourseResult(
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
	public static CourseResult of(CourseInfo courseInfo) {
		return new CourseResult(
				courseInfo.id(),
				courseInfo.creatorId(),
				courseInfo.title(),
				courseInfo.description(),
				courseInfo.status(),
				courseInfo.price(),
				courseInfo.capacity(),
				courseInfo.startDate(),
				courseInfo.endDate()
		);
	}

	public record Detail(
			Long id,
			Long creatorId,
			String title,
			String description,
			String status,
			int price,
			int capacity,
			LocalDate startDate,
			LocalDate endDate,
			int applicants
	){
		public static CourseResult.Detail of(CourseInfo courseInfo, int applicants){
			return new CourseResult.Detail(
					courseInfo.id(),
					courseInfo.creatorId(),
					courseInfo.title(),
					courseInfo.description(),
					courseInfo.status(),
					courseInfo.price(),
					courseInfo.capacity(),
					courseInfo.startDate(),
					courseInfo.endDate(),
					applicants
			);
		}
	}

}
