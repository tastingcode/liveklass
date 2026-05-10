package com.liveklass.domain.enrollment;

import java.util.List;

public interface EnrollmentRepository {
	EnrollmentEntity save(EnrollmentEntity enrollment);

	long countByCourseIdAndStatusNot(Long courseId, EnrollmentStatus status);

	List<EnrollmentEntity> findAllByUserId(Long userId);
}
