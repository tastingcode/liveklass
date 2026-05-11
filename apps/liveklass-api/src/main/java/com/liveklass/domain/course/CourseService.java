package com.liveklass.domain.course;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
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
	public Optional<CourseEntity> findCourse(CourseCommand.Find command) {
		return courseRepository.findById(command.courseId());
	}

	@Transactional(readOnly = true)
	public Optional<CourseInfo> findCourseInfo(CourseCommand.Find command) {
		return courseRepository.findById(command.courseId()).map(CourseInfo::from);
	}

	@Transactional
	public Optional<CourseInfo> findCourseInfoForUpdate(CourseCommand.Find command) {
		return courseRepository.findByIdForUpdate(command.courseId()).map(CourseInfo::from);
	}

	@Transactional(readOnly = true)
	public List<CourseInfo> findCourses(CourseCommand.Search command) {
		CourseStatus status = CourseStatus.from(command.status());

		return courseRepository.findAllByStatus(status, command.page(), command.size())
				.stream()
				.map(CourseInfo::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public long countCourses(CourseCommand.Search command) {
		CourseStatus status = CourseStatus.from(command.status());
		return courseRepository.countByStatus(status, command.page(), command.size());
	}

	@Transactional
	public CourseInfo courseOpen(CourseCommand.Open command) {
		CourseEntity course = courseRepository.findById(command.courseId()).orElseThrow(
				() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다.")
		);

		if (!course.getCreatorId().equals(command.creatorId())) {
			throw new CoreException(ErrorType.FORBIDDEN, "강사는 본인의 강의만 오픈할 수 있습니다.");
		}

		course.open();
		return CourseInfo.from(course);
	}

	@Transactional
	public CourseInfo courseClose(CourseCommand.Close command) {
		CourseEntity course = courseRepository.findById(command.courseId()).orElseThrow(
				() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다.")
		);

		if (!course.getCreatorId().equals(command.creatorId())) {
			throw new CoreException(ErrorType.FORBIDDEN, "강사는 본인의 강의만 마감할 수 있습니다.");
		}

		course.close();
		return CourseInfo.from(course);
	}

}
