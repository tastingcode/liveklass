package com.liveklass.application.enrollment;

import com.liveklass.application.user.UserValidator;
import com.liveklass.domain.common.PageResponse;
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

		// 1. 강의 조회 및 락 획득
		CourseInfo courseInfo = courseService.findCourseInfoForUpdate(criteria.toCourseFind())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다: " + criteria.courseId()));

		// 2. 수강생 유효셩 검증
		userValidator.validateStudent(new UserCommand.Find(criteria.userId()));

		// 3. 강의 상태 확인
		if (CourseStatus.OPEN != CourseStatus.from(courseInfo.status())) {
			throw new CoreException(ErrorType.BAD_REQUEST, "모집 중인 강의만 수강 신청할 수 있습니다.");
		}

		// 4. 수강 신청 인원 확인
		int applicants = enrollmentService.getApplicantsCount(criteria.courseId());

		// 5. 수강 신청 등록
		EnrollmentInfo enrollmentInfo = enrollmentService.enroll(criteria.toEnrollmentCreate(courseInfo.capacity()), applicants);
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

	@Transactional(readOnly = true)
	public PageResponse<EnrollmentResult> getCourseStudents(EnrollmentCriteria.GetCourseStudents criteria) {

		// 1. 강사 유효성 검증
		userValidator.validateCreator(new UserCommand.Find(criteria.creatorId()));

		// 2. 강의 조회 및 소유권 검증
		CourseInfo courseInfo = courseService.findCourseInfo(criteria.toCourseFind())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "강의를 찾을 수 없습니다: " + criteria.courseId()));

		if (!courseInfo.creatorId().equals(criteria.creatorId())) {
			throw new CoreException(ErrorType.FORBIDDEN, "강사는 본인의 강의 수강생만 조회할 수 있습니다.");
		}

		// 3. 수강 확정 학생 목록 조회
		List<EnrollmentResult> students = enrollmentService.findCourseStudents(criteria.toEnrollmentFindCourseStudents()).stream()
				.map(EnrollmentResult::of)
				.toList();
		long totalElements = enrollmentService.countCourseStudents(criteria.toEnrollmentFindCourseStudents());

		int pageSize = criteria.pageable().getPageSize();
		int totalPage = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

		return PageResponse.of(
				students,
				criteria.pageable().getPageNumber(),
				pageSize,
				totalElements,
				totalPage
		);
	}

	@Transactional
	public EnrollmentResult enrollmentConfirm(EnrollmentCriteria.Confirm criteria) {
		// 1. 수강생 유효성 검증
		userValidator.validateStudent(new UserCommand.Find(criteria.userId()));

		// 2. 수강 상태 업데이트
		EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentConfirm(criteria.toEnrollmentConfirm());

		return EnrollmentResult.of(enrollmentInfo);
	}

	@Transactional
	public EnrollmentResult enrollmentCancel(EnrollmentCriteria.Cancel criteria) {
		// 1. 수강생 유효성 검증
		userValidator.validateStudent(new UserCommand.Find(criteria.userId()));

		// 2. 강의 수강 취소
		EnrollmentInfo enrollmentInfo = enrollmentService.enrollmentCancel(criteria.toEnrollmentCancel());

		return EnrollmentResult.of(enrollmentInfo);
	}

}
