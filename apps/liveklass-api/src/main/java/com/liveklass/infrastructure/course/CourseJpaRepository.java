package com.liveklass.infrastructure.course;

import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseJpaRepository extends JpaRepository<CourseEntity, Long> {
	List<CourseEntity> findAllByStatus(CourseStatus status);

}
