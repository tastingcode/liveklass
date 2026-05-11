package com.liveklass.application.enrollment;

import com.liveklass.application.user.UserValidator;
import com.liveklass.domain.course.CourseInfo;
import com.liveklass.domain.course.CourseService;
import com.liveklass.domain.course.CourseStatus;
import com.liveklass.domain.enrollment.EnrollmentInfo;
import com.liveklass.domain.enrollment.EnrollmentService;
import com.liveklass.domain.user.UserCommand;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EnrollmentFacade {
	private final EnrollmentService enrollmentService;
	private final UserValidator userValidator;
	private final CourseService courseService;

	@Transactional
	public EnrollmentResult enroll(EnrollmentCriteria.Enroll criteria) {

		// 수강생 유효셩 검증
		userValidator.validateStudent(new UserCommand.Find(criteria.userId()));

		// 강의 조회
		CourseInfo courseInfo = courseService.findCourseInfo(criteria.toCourseFind())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다: " + criteria.courseId()));

		// 강의 상태 확인
		if (CourseStatus.OPEN != CourseStatus.from(courseInfo.status())) {
			throw new CoreException(ErrorType.BAD_REQUEST, "모집 중인 강의만 수강 신청할 수 있습니다.");
		}

		// 수강 신청 등록
		EnrollmentInfo enrollmentInfo = enrollmentService.enroll(criteria.toEnrollmentCreate(courseInfo.capacity()));

		return EnrollmentResult.of(enrollmentInfo);
	}

	@Transactional(readOnly = true)
	public List<EnrollmentResult> getMyEnrollments(EnrollmentCriteria.GetMy criteria) {
		// 수강생 유효셩 검증
		userValidator.validateStudent(new UserCommand.Find(criteria.userId()));

		return enrollmentService.findMyEnrollments(criteria.toEnrollmentFindMy()).stream()
				.map(EnrollmentResult::of)
				.toList();
	}

}
