package com.maxplus1.db.starter.config.common;

import javax.servlet.http.HttpServletResponse;

public enum HttpStatusCode {
	OK(HttpServletResponse.SC_OK), NOT_FOUND(HttpServletResponse.SC_NOT_FOUND),
	ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
	TIMEOUT(HttpServletResponse.SC_REQUEST_TIMEOUT);
	
	private int code;

	private HttpStatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
