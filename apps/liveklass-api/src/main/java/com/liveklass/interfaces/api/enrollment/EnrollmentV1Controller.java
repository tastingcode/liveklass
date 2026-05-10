package com.liveklass.interfaces.api.enrollment;

import com.liveklass.application.enrollment.EnrollmentCriteria;
import com.liveklass.application.enrollment.EnrollmentFacade;
import com.liveklass.application.enrollment.EnrollmentResult;
import com.liveklass.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentV1Controller implements EnrollmentV1ApiSpec {
	private final EnrollmentFacade enrollmentFacade;

	@PostMapping("")
	@Override
	public ApiResponse<EnrollmentV1Dto.EnrollmentResponse> enroll(
			@RequestHeader("X-USER-ID") Long userId,
			@Valid @RequestBody EnrollmentV1Dto.EnrollRequest request
	) {
		EnrollmentResult enrollmentResult = enrollmentFacade.enroll(request.toCriteria(userId));
		EnrollmentV1Dto.EnrollmentResponse response = EnrollmentV1Dto.EnrollmentResponse.from(enrollmentResult);
		return ApiResponse.success(response);
	}

	@GetMapping("/me")
	@Override
	public ApiResponse<List<EnrollmentV1Dto.EnrollmentResponse>> getMyEnrollments(
			@RequestHeader("X-USER-ID") Long userId
	) {
		List<EnrollmentV1Dto.EnrollmentResponse> response = enrollmentFacade.getMyEnrollments(new EnrollmentCriteria.GetMy(userId)).stream()
				.map(EnrollmentV1Dto.EnrollmentResponse::from)
				.toList();
		return ApiResponse.success(response);
	}
}
