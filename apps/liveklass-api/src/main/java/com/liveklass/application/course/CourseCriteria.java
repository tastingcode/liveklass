package com.liveklass.application.course;

import com.liveklass.domain.course.CourseCommand;

import java.time.LocalDate;

public class CourseCriteria {
	public record Register(
			Long creatorId,
			String title,
			String description,
			int price,
			int capacity,
			LocalDate startDate,
			LocalDate endDate
	) {
		public CourseCommand.Create toCourseCreate() {
			return new CourseCommand.Create(
					creatorId,
					title,
					description,
					price,
					capacity,
					startDate,
					endDate
			);
		}
	}

	public record Get(Long courseId) {
		public CourseCommand.Find toCourseFind() {
			return new CourseCommand.Find(courseId);
		}
	}

	public record Search(String status) {
		public CourseCommand.Search toCourseSearch() {
			return new CourseCommand.Search(status);
		}
	}
}
