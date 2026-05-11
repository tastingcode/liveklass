package com.liveklass.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
	EnrollmentEntity save(EnrollmentEntity enrollment);

	int countByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses);

	List<EnrollmentEntity> findAllByUserId(Long userId);

	List<EnrollmentEntity> findAllByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, int page, int size);

	long countByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, int page, int size);

	Optional<EnrollmentEntity> findByCourseIdAndUserId(Long courseId, Long userId);
}
