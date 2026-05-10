package com.liveklass.domain.user;

import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;

public enum UserRole {
	CREATOR,
	STUDENT;

	public static UserRole from(String role){
		for (UserRole r : values()) {
			if (r.name().equals(role)){
				return r;
			}
		}

		throw new CoreException(ErrorType.BAD_REQUEST, "유저 역할은 두 가지 중 선택해야 합니다.");
	}
}
