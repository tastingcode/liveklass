package com.liveklass.domain.enrollment;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;

public enum EnrollmentStatus {
	PENDING,
	CONFIRMED,
	CANCELLED;

	public static EnrollmentStatus from(String status){
		for (EnrollmentStatus s : values()) {
			if (s.name().equals(status)){
				return s;
			}
		}

		throw new CoreException(ErrorType.BAD_REQUEST, "수강 신청 상태는 세 가지 중 선택해야 합니다.");
	}
}
