package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EnrollmentService {
	private static final int CANCEL_POLICY_DAYS = 7;
	private static final List<EnrollmentStatus> APPLICANT_STATUSES = List.of(
			EnrollmentStatus.PENDING,
			EnrollmentStatus.CONFIRMED
	);
	private static final List<EnrollmentStatus> COURSE_STUDENT_STATUSES = List.of(EnrollmentStatus.CONFIRMED);

	private final EnrollmentRepository enrollmentRepository;

	@Transactional
	public EnrollmentInfo enroll(EnrollmentCommand.Create command, int applicants) {
		if (applicants >= command.courseCapacity()) {
			throw new CoreException(ErrorType.CONFLICT, "수강 정원이 초과되었습니다.");
		}

		EnrollmentEntity enrollment = EnrollmentEntity.from(command);

		return EnrollmentInfo.from(enrollmentRepository.save(enrollment));
	}

	@Transactional(readOnly = true)
	public List<EnrollmentInfo> findMyEnrollments(EnrollmentCommand.FindMy command) {
		return enrollmentRepository.findAllByUserId(command.userId()).stream()
				.map(EnrollmentInfo::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<EnrollmentInfo> findCourseStudents(EnrollmentCommand.FindCourseStudents command) {
		return enrollmentRepository.findAllByCourseIdAndStatusIn(
						command.courseId(),
						COURSE_STUDENT_STATUSES,
						command.page(),
						command.size()
				).stream()
				.map(EnrollmentInfo::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public long countCourseStudents(EnrollmentCommand.FindCourseStudents command) {
		return enrollmentRepository.countByCourseIdAndStatusIn(
				command.courseId(),
				COURSE_STUDENT_STATUSES,
				command.page(),
				command.size()
		);
	}

	@Transactional(readOnly = true)
	public int getApplicantsCount(Long courseId){
		return enrollmentRepository.countByCourseIdAndStatusIn(courseId, APPLICANT_STATUSES);
	}

	@Transactional
	public EnrollmentInfo enrollmentConfirm(EnrollmentCommand.Confirm command) {
		EnrollmentEntity enrollment = enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "수강 신청을 찾을 수 없습니다."));

		enrollment.confirm();
		return EnrollmentInfo.from(enrollment);
	}

	@Transactional
	public EnrollmentInfo enrollmentCancel(EnrollmentCommand.Cancel command) {
		EnrollmentEntity enrollment = enrollmentRepository.findByCourseIdAndUserId(command.courseId(), command.userId())
				.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "수강 신청을 찾을 수 없습니다."));

		enrollment.cancel(LocalDate.now(), CANCEL_POLICY_DAYS);
		return EnrollmentInfo.from(enrollment);
	}
}
