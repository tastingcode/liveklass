package com.liveklass.application.enrollment;

import com.liveklass.domain.course.CourseCommand;
import com.liveklass.domain.enrollment.EnrollmentCommand;
import org.springframework.data.domain.Pageable;

public class EnrollmentCriteria {
	public record Enroll(Long courseId, Long userId) {
		public EnrollmentCommand.Create toEnrollmentCreate(int courseCapacity) {
			return new EnrollmentCommand.Create(courseId, userId, courseCapacity);
		}

		public CourseCommand.Find toCourseFind(){
			return new CourseCommand.Find(courseId);}
	}

	public record GetMy(Long userId) {
		public EnrollmentCommand.FindMy toEnrollmentFindMy() {
			return new EnrollmentCommand.FindMy(userId);
		}
	}

	public record GetCourseStudents(Long courseId, Long creatorId, Pageable pageable) {
		public CourseCommand.Find toCourseFind() {
			return new CourseCommand.Find(courseId);
		}

		public EnrollmentCommand.FindCourseStudents toEnrollmentFindCourseStudents() {
			return new EnrollmentCommand.FindCourseStudents(courseId, pageable.getPageNumber(), pageable.getPageSize());
		}
	}

	public record Confirm(Long courseId, Long userId) {
		public EnrollmentCommand.Confirm toEnrollmentConfirm() {
			return new EnrollmentCommand.Confirm(courseId, userId);
		}
	}

	public record Cancel(Long courseId, Long userId) {
		public EnrollmentCommand.Cancel toEnrollmentCancel() {
			return new EnrollmentCommand.Cancel(courseId, userId);
		}
	}
}
