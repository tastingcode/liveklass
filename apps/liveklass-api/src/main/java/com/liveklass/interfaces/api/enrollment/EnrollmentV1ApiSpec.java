package com.liveklass.interfaces.api.enrollment;

import com.liveklass.domain.common.PageResponse;
import com.liveklass.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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

	@Operation(
			summary = "강의 수강생 목록 조회",
			description = "강사가 본인의 특정 강의를 수강 확정한 학생 목록을 조회합니다."
	)
	ApiResponse<PageResponse<EnrollmentV1Dto.EnrollmentResponse>> getCourseStudents(
			@Schema(name = "사용자 ID", description = "강사 사용자 ID")
			Long userId,
			@Schema(name = "강의 ID", description = "수강생 목록을 조회할 강의 ID")
			Long courseId,
			@PageableDefault(page = 0, size = 10) Pageable pageable
	);

	@Operation(
			summary = "수강 신청 결제 확정",
			description = "X-USER-ID 헤더와 강의 ID로 수강 신청을 찾아 결제 완료 상태로 변경합니다."
	)
	ApiResponse<EnrollmentV1Dto.EnrollmentResponse> confirmPayment(
			@Schema(name = "사용자 ID", description = "결제 확정하는 사용자 ID")
			Long userId,
			@Schema(name = "강의 ID", description = "결제 확정할 강의 ID")
			Long courseId
	);

	@Operation(
			summary = "수강 신청 취소",
			description = "X-USER-ID 헤더와 강의 ID로 수강 신청을 찾아 취소 상태로 변경합니다."
	)
	ApiResponse<EnrollmentV1Dto.EnrollmentResponse> cancelEnrollment(
			@Schema(name = "사용자 ID", description = "수강 취소하는 사용자 ID")
			Long userId,
			@Schema(name = "강의 ID", description = "수강 취소할 강의 ID")
			Long courseId
	);
}
