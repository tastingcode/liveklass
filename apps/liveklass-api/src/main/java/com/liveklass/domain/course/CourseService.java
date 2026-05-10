package com.liveklass.domain.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseService {
	private final CourseRepository courseRepository;

	@Transactional
	public CourseInfo create(CourseCommand.Create command) {
		CourseEntity course = CourseEntity.from(command);

		return CourseInfo.from(courseRepository.save(course));
	}

	@Transactional(readOnly = true)
	public Optional<CourseInfo> findCourse(CourseCommand.Find command) {
		return courseRepository.findById(command.courseId()).map(CourseInfo::from);
	}

	@Transactional(readOnly = true)
	public List<CourseInfo> findCourses(CourseCommand.Search command) {
		CourseStatus status = command.toCourseStatus();
		List<CourseEntity> courses = status == null
				? courseRepository.findAll()
				: courseRepository.findAllByStatus(status);

		return courses.stream()
				.map(CourseInfo::from)
				.toList();
	}

}
