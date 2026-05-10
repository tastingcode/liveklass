package com.liveklass.support.error;

import org.springframework.http.HttpStatus;

public enum ErrorType {
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "일시적인 오류가 발생했습니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "잘못된 요청입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "존재하지 않는 리소스입니다."),
	CONFLICT(HttpStatus.CONFLICT, "Conflict", "이미 존재하는 리소스입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden", "권한이 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorType(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
