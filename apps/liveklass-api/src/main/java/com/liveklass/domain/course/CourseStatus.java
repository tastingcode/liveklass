package com.liveklass.domain.course;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;

public enum CourseStatus {
	DRAFT, OPEN, CLOSED;

	public static CourseStatus from(String status){
		for (CourseStatus s : values()) {
			if (s.name().equals(status)){
				return s;
			}
		}

		throw new CoreException(ErrorType.BAD_REQUEST, "강의 상태는 세 가지 중 선택해야 합니다.");
	}
}
