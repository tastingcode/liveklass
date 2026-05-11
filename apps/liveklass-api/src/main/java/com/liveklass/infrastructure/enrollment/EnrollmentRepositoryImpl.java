package com.liveklass.infrastructure.enrollment;

import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentRepository;
import com.liveklass.domain.enrollment.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
	private final EnrollmentJpaRepository enrollmentJpaRepository;

	@Override
	public EnrollmentEntity save(EnrollmentEntity enrollment) {
		return enrollmentJpaRepository.save(enrollment);
	}

	@Override
	public int countByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses) {
		return enrollmentJpaRepository.countByCourseIdAndStatusIn(courseId, statuses);
	}

	@Override
	public Optional<EnrollmentEntity> findByCourseIdAndUserId(Long courseId, Long userId) {
		return enrollmentJpaRepository.findByCourseIdAndUserId(courseId, userId);
	}

	@Override
	public List<EnrollmentEntity> findAllByUserId(Long userId) {
		return enrollmentJpaRepository.findAllByUserId(userId);
	}

	@Override
	public List<EnrollmentEntity> findAllByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, int page, int size) {
		return enrollmentJpaRepository.findAllByCourseIdAndStatusIn(courseId, statuses, PageRequest.of(page, size));
	}

	@Override
	public long countByCourseIdAndStatusIn(Long courseId, List<EnrollmentStatus> statuses, int page, int size) {
		return enrollmentJpaRepository.findByCourseIdAndStatusIn(courseId, statuses, PageRequest.of(page, size)).getTotalElements();
	}
}
