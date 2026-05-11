package com.liveklass.interfaces.api.course;

import com.liveklass.application.course.CourseCriteria;
import com.liveklass.application.course.CourseFacade;
import com.liveklass.application.course.CourseResult;
import com.liveklass.domain.common.PageResponse;
import com.liveklass.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

	@Override
	@GetMapping
	public ApiResponse<PageResponse<CourseV1Dto.CourseResponse>> getCourses(
			@RequestParam(required = false, defaultValue = "OPEN") String status,
			@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		PageResponse<CourseResult> results = courseFacade.getCourses(new CourseCriteria.Search(status, pageable));
		PageResponse<CourseV1Dto.CourseResponse> response = results.map(CourseV1Dto.CourseResponse::from);

		return ApiResponse.success(response);
	}

	@GetMapping("/{courseId}")
	@Override
	public ApiResponse<CourseV1Dto.CourseResponse> getCourse(
			@PathVariable Long courseId
	) {
		CourseResult.Detail result = courseFacade.getCourse(new CourseCriteria.Get(courseId));
		CourseV1Dto.CourseResponse response = CourseV1Dto.CourseResponse.from(result);
		return ApiResponse.success(response);
	}

	@PatchMapping("/{courseId}/open")
	@Override
	public ApiResponse<CourseV1Dto.CourseResponse> openCourse(
			@RequestHeader("X-USER-ID") Long userId,
			@PathVariable Long courseId
	) {
		CourseResult courseResult = courseFacade.open(new CourseCriteria.Open(courseId, userId));
		CourseV1Dto.CourseResponse response = CourseV1Dto.CourseResponse.from(courseResult);
		return ApiResponse.success(response);
	}

	@PatchMapping("/{courseId}/close")
	@Override
	public ApiResponse<CourseV1Dto.CourseResponse> closeCourse(
			@RequestHeader("X-USER-ID") Long userId,
			@PathVariable Long courseId
	) {
		CourseResult courseResult = courseFacade.close(new CourseCriteria.Close(courseId, userId));
		CourseV1Dto.CourseResponse response = CourseV1Dto.CourseResponse.from(courseResult);
		return ApiResponse.success(response);
	}

}
