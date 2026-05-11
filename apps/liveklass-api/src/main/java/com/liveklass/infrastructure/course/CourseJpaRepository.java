package com.liveklass.infrastructure.course;

import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<CourseEntity, Long> {
	List<CourseEntity> findAllByStatus(CourseStatus status, Pageable pageable);

	Page<CourseEntity> findByStatus(CourseStatus status, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CourseEntity c where c.id = :courseId")
	Optional<CourseEntity> findByIdForUpdate(@Param("courseId") Long courseId);
}
