package com.liveklass.application.course;

import com.liveklass.domain.course.CourseInfo;
import com.liveklass.domain.course.CourseService;
import com.liveklass.domain.user.UserCommand;
import com.liveklass.domain.user.UserInfo;
import com.liveklass.domain.user.UserRole;
import com.liveklass.domain.user.UserService;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CourseFacade {
	private final CourseService courseService;
	private final UserService userService;

	@Transactional
	public CourseResult registerCourse(CourseCriteria.Register criteria) {
		UserInfo creator = userService.findUser(new UserCommand.Find(criteria.creatorId()))
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: " + criteria.creatorId()));

		if (!UserRole.CREATOR.name().equals(creator.userRole())) {
			throw new CoreException(ErrorType.FORBIDDEN, "강사만 강의를 등록할 수 있습니다.");
		}

		CourseInfo courseInfo = courseService.create(criteria.toCourseCreate());

		return CourseResult.of(courseInfo);
	}

	@Transactional(readOnly = true)
	public CourseResult getCourse(CourseCriteria.Get criteria) {
		CourseInfo courseInfo = courseService.findCourse(criteria.toCourseFind())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다: " + criteria.courseId()));

		return CourseResult.of(courseInfo);
	}

	@Transactional(readOnly = true)
	public List<CourseResult> getCourses(CourseCriteria.Search criteria) {
		return courseService.findCourses(criteria.toCourseSearch()).stream()
				.map(CourseResult::of)
				.toList();
	}
}
