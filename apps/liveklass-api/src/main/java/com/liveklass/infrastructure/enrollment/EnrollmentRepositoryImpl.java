package com.liveklass.infrastructure.enrollment;

import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentRepository;
import com.liveklass.domain.enrollment.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
	private final EnrollmentJpaRepository enrollmentJpaRepository;

	@Override
	public EnrollmentEntity save(EnrollmentEntity enrollment) {
		return enrollmentJpaRepository.save(enrollment);
	}

	@Override
	public long countByCourseIdAndStatusNot(Long courseId, EnrollmentStatus status) {
		return enrollmentJpaRepository.countByCourseIdAndStatusNot(courseId, status);
	}

	@Override
	public List<EnrollmentEntity> findAllByUserId(Long userId) {
		return enrollmentJpaRepository.findAllByUserId(userId);
	}
}
