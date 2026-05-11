package com.liveklass.interfaces.api.course;

import com.liveklass.domain.common.PageResponse;
import com.liveklass.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

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
			description = "강의 목록 정보를 조회합니다."
	)
	ApiResponse<PageResponse<CourseV1Dto.CourseResponse>> getCourses(
			@Parameter(
					name = "status",
					description = "강의 상태",
					required = false
			)
			@RequestParam(required = false) String status,
			@Parameter(
					name = "pageable",
					description = "페이징 정보 (쿼리 파라미터)",
					required = false
			)
			@PageableDefault(page = 0, size = 10) Pageable pageable
	);

	@Operation(
			summary = "강의 상세 조회",
			description = "강의 단건 정보를 조회합니다."
	)
	ApiResponse<CourseV1Dto.CourseResponse> getCourse(
			@Schema(name = "강의 ID", description = "조회할 강의 ID")
			Long courseId
	);

	@Operation(
			summary = "강의 오픈",
			description = "강사가 본인의 초안 강의를 모집 중 상태로 변경합니다."
	)
	ApiResponse<CourseV1Dto.CourseResponse> openCourse(
			@Schema(name = "사용자 ID", description = "강의를 오픈하는 사용자 ID")
			Long userId,
			@Schema(name = "강의 ID", description = "오픈할 강의 ID")
			Long courseId
	);

	@Operation(
			summary = "강의 모집 마감",
			description = "강사가 본인의 강의를 모집 마감 상태로 변경합니다."
	)
	ApiResponse<CourseV1Dto.CourseResponse> closeCourse(
			@Schema(name = "사용자 ID", description = "강의를 마감하는 사용자 ID")
			Long userId,
			@Schema(name = "강의 ID", description = "마감할 강의 ID")
			Long courseId
	);
}
