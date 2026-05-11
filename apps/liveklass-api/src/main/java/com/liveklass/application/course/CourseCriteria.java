package com.liveklass.application.course;

import com.liveklass.domain.course.CourseCommand;
import org.springframework.data.domain.Pageable;

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

	public record Search(String status, Pageable pageable){
		public CourseCommand.Search toSearch(){
			return new CourseCommand.Search(status, pageable.getPageNumber(), pageable.getPageSize());
		}
	}

	public record Get(Long courseId) {
		public CourseCommand.Find toCourseFind() {
			return new CourseCommand.Find(courseId);
		}
	}

	public record Open(Long courseId, Long creatorId){
		public CourseCommand.Open toOpen(){
			return new CourseCommand.Open(courseId, creatorId);
		}

	}

	public record Close(Long courseId, Long creatorId){
		public CourseCommand.Close toClose(){
			return new CourseCommand.Close(courseId, creatorId);
		}

	}
}
