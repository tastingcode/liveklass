package com.liveklass.domain.course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
	CourseEntity save(CourseEntity course);

	Optional<CourseEntity> findById(Long courseId);

	List<CourseEntity> findAllByStatus(CourseStatus status, int page, int size);

	long countByStatus(CourseStatus status, int page, int size);
}
