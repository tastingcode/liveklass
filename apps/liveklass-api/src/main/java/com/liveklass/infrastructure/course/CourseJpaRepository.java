package com.liveklass.infrastructure.course;

import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseJpaRepository extends JpaRepository<CourseEntity, Long> {
	List<CourseEntity> findAllByStatus(CourseStatus status, Pageable pageable);

	Page<CourseEntity> findByStatus(CourseStatus status, Pageable pageable);
}
