package com.liveklass.application.enrollment;

import com.liveklass.domain.course.CourseCommand;
import com.liveklass.domain.enrollment.EnrollmentCommand;

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
}
