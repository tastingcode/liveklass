package com.liveklass.domain.course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
	CourseEntity save(CourseEntity course);

	Optional<CourseEntity> findById(Long courseId);

	List<CourseEntity> findAll();

	List<CourseEntity> findAllByStatus(CourseStatus status);
}
