package com.liveklass.interfaces.api.course;

import com.liveklass.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Course V1 API", description = "Course API 입니다.")
public interface CourseV1ApiSpec {
	@Operation(
			summary = "강의 등록",
			description = "CREATOR 역할의 사용자가 강의를 등록합니다."
	)
	ApiResponse<CourseV1Dto.CourseResponse> registerCourse(
			@Schema(name = "사용자 ID", description = "강의를 등록하는 사용자 ID")
			Long userId,
			@Schema(name = "강의 등록 요청", description = "강의 등록에 필요한 정보")
			CourseV1Dto.RegisterRequest request
	);

	@Operation(
			summary = "강의 목록 조회",
			description = "강의 목록을 조회합니다. status 값으로 DRAFT, OPEN, CLOSED 필터를 사용할 수 있습니다."
	)
	ApiResponse<List<CourseV1Dto.CourseResponse>> getCourses(
			@Schema(name = "강의 상태", description = "DRAFT, OPEN, CLOSED 중 하나")
			String status
	);

	@Operation(
			summary = "강의 상세 조회",
			description = "강의 단건 정보를 조회합니다."
	)
	ApiResponse<CourseV1Dto.CourseResponse> getCourse(
			@Schema(name = "강의 ID", description = "조회할 강의 ID")
			Long courseId
	);
}
