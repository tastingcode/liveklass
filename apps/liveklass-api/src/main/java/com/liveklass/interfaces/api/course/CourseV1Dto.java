package com.liveklass.interfaces.api.course;

import com.liveklass.application.course.CourseCriteria;
import com.liveklass.application.course.CourseResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class CourseV1Dto {
	public record RegisterRequest(
			@NotBlank(message = "강의 제목은 필수입니다.")
			String title,

			@NotBlank(message = "강의 설명은 필수입니다.")
			String description,

			@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
			int price,

			@Positive(message = "정원은 1명 이상이어야 합니다.")
			int capacity,

			@NotNull(message = "수강 시작일은 필수입니다.")
			LocalDate startDate,

			@NotNull(message = "수강 종료일은 필수입니다.")
			LocalDate endDate
	) {
		public CourseCriteria.Register toCriteria(Long creatorId) {
			return new CourseCriteria.Register(
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

	public record CourseResponse(
			Long id,
			Long creatorId,
			String title,
			String description,
			String status,
			int price,
			int capacity,
			LocalDate startDate,
			LocalDate endDate,
			Integer applicants
	) {
		public static CourseResponse from(CourseResult courseResult) {
			return new CourseResponse(
					courseResult.id(),
					courseResult.creatorId(),
					courseResult.title(),
					courseResult.description(),
					courseResult.status(),
					courseResult.price(),
					courseResult.capacity(),
					courseResult.startDate(),
					courseResult.endDate(),
					null
			);
		}

		public static CourseResponse from(CourseResult.Detail result) {
			return new CourseResponse(
					result.id(),
					result.creatorId(),
					result.title(),
					result.description(),
					result.status(),
					result.price(),
					result.capacity(),
					result.startDate(),
					result.endDate(),
					result.applicants()
			);
		}
	}
}
