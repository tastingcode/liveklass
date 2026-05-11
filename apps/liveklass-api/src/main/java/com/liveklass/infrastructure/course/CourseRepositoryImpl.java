package com.liveklass.infrastructure.course;

import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseRepository;
import com.liveklass.domain.course.CourseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
	private final CourseJpaRepository courseJpaRepository;

	@Override
	public CourseEntity save(CourseEntity course) {
		return courseJpaRepository.save(course);
	}


	@Override
	public Optional<CourseEntity> findById(Long courseId) {
		return courseJpaRepository.findById(courseId);
	}

	@Override
	public List<CourseEntity> findAllByStatus(CourseStatus status, int page, int size) {
		return courseJpaRepository.findAllByStatus(status, PageRequest.of(page, size));
	}

	@Override
	public long countByStatus(CourseStatus status, int page, int size) {
		return courseJpaRepository.findByStatus(status, PageRequest.of(page, size)).getTotalElements();
	}

}
