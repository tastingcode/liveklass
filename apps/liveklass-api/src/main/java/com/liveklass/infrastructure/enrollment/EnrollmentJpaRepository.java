package com.liveklass.infrastructure.enrollment;

import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, Long> {
	long countByCourseIdAndStatusNot(Long courseId, EnrollmentStatus status);

	List<EnrollmentEntity> findAllByUserId(Long userId);

	int countByCourseId(Long courseId);
}
