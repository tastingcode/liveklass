package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EnrollmentService {
	private final EnrollmentRepository enrollmentRepository;

	@Transactional
	public EnrollmentInfo enroll(EnrollmentCommand.Create command) {

		long enrolledCount = enrollmentRepository.countByCourseIdAndStatusNot(command.courseId(), EnrollmentStatus.CANCELLED);
		if (enrolledCount >= command.courseCapacity()) {
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
}
