package com.liveklass.infrastructure.enrollment;

import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, Long> {
	int countByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses);

	List<EnrollmentEntity> findAllByUserId(Long userId);

	List<EnrollmentEntity> findAllByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, Pageable pageable);

	Page<EnrollmentEntity> findByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, Pageable pageable);

	Optional<EnrollmentEntity> findByCourseIdAndUserId(Long courseId, Long userId);
}
