package com.liveklass.interfaces.api.course;

import com.liveklass.application.course.CourseCriteria;
import com.liveklass.application.course.CourseFacade;
import com.liveklass.application.course.CourseResult;
import com.liveklass.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/courses")
public class CourseV1Controller implements CourseV1ApiSpec {
	private final CourseFacade courseFacade;

	@PostMapping("")
	@Override
	public ApiResponse<CourseV1Dto.CourseResponse> registerCourse(
			@RequestHeader("X-USER-ID") Long userId,
			@Valid @RequestBody CourseV1Dto.RegisterRequest request
	) {
		CourseResult courseResult = courseFacade.registerCourse(request.toCriteria(userId));
		CourseV1Dto.CourseResponse response = CourseV1Dto.CourseResponse.from(courseResult);
		return ApiResponse.success(response);
	}

	@GetMapping("")
	@Override
	public ApiResponse<List<CourseV1Dto.CourseResponse>> getCourses(
			@RequestParam(required = false) String status
	) {
		List<CourseV1Dto.CourseResponse> response = courseFacade.getCourses(new CourseCriteria.Search(status)).stream()
				.map(CourseV1Dto.CourseResponse::from)
				.toList();
		return ApiResponse.success(response);
	}

	@GetMapping("/{courseId}")
	@Override
	public ApiResponse<CourseV1Dto.CourseResponse> getCourse(
			@PathVariable Long courseId
	) {
		CourseResult courseResult = courseFacade.getCourse(new CourseCriteria.Get(courseId));
		CourseV1Dto.CourseResponse response = CourseV1Dto.CourseResponse.from(courseResult);
		return ApiResponse.success(response);
	}
}
