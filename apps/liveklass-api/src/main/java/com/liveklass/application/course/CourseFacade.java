package com.liveklass.application.course;

import com.liveklass.domain.common.PageResponse;
import com.liveklass.domain.course.CourseInfo;
import com.liveklass.domain.course.CourseService;
import com.liveklass.domain.enrollment.EnrollmentService;
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
	private final CourseValidator courseValidator;
	private final EnrollmentService enrollmentService;

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
	public PageResponse<CourseResult> getCourses(CourseCriteria.Search criteria){

		// 1. 강의 목록 조회
		List<CourseResult> courses = courseService.findCourses(criteria.toSearch()).stream()
				.map(CourseResult::of)
				.toList();

		// 2. 강의 카운트 조회
		long totalElements = courseService.countCourses(criteria.toSearch());

		// 3. PageResponse 조합
		int pageSize = criteria.pageable().getPageSize();
		int totalPage = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

		return PageResponse.of(
				courses,
				criteria.pageable().getPageNumber(),
				pageSize,
				totalElements,
				totalPage
		);
	}

	@Transactional(readOnly = true)
	public CourseResult.Detail getCourse(CourseCriteria.Get criteria) {

		// 1. 강의 정보 조회
		CourseInfo courseInfo = courseService.findCourseInfo(criteria.toCourseFind())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다: " + criteria.courseId()));

		// 2. 해당 신청 인원 조회
		int enrollmentCount = enrollmentService.getApplicantsCount(criteria.courseId());

		return CourseResult.Detail.of(courseInfo, enrollmentCount);
	}

	@Transactional
	public CourseResult open(CourseCriteria.Open criteria) {
		// 강의 오픈
		CourseInfo courseInfo = courseService.courseOpen(criteria.toOpen());

		return CourseResult.of(courseInfo);
	}

	@Transactional
	public CourseResult close(CourseCriteria.Close criteria) {
		// 강의 모집 마감
		CourseInfo courseInfo = courseService.courseClose(criteria.toClose());

		return CourseResult.of(courseInfo);
	}

}
