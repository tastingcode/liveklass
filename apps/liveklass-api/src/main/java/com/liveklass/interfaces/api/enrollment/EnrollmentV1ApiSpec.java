package com.liveklass.interfaces.api.enrollment;

import com.liveklass.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Enrollment V1 API", description = "Enrollment API 입니다.")
public interface EnrollmentV1ApiSpec {
	@Operation(
			summary = "수강 신청",
			description = "수강생이 모집 중인 강의에 수강 신청합니다."
	)
	ApiResponse<EnrollmentV1Dto.EnrollmentResponse> enroll(
			@Schema(name = "사용자 ID", description = "수강 신청하는 사용자 ID")
			Long userId,
			@Schema(name = "수강 신청 요청", description = "수강 신청에 필요한 정보")
			EnrollmentV1Dto.EnrollRequest request
	);

	@Operation(
			summary = "내 수강 신청 목록 조회",
			description = "X-USER-ID 헤더로 내 수강 신청 목록을 조회합니다."
	)
	ApiResponse<List<EnrollmentV1Dto.EnrollmentResponse>> getMyEnrollments(
			@Schema(name = "사용자 ID", description = "사용자 ID")
			Long userId
	);
}
