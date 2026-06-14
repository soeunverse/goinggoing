package com.goinggoing.goinggoing.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

	private static final String START_TIME_ATTRIBUTE = "apiStartTime";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
		log.info("[API 요청] {} {}", request.getMethod(), request.getRequestURI());
		return true;
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception exception
	) {
		long elapsedMillis = getElapsedMillis(request);
		if (exception == null) {
			log.info("[API 응답] {} {} status={} time={}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMillis);
			return;
		}
		log.warn("[API 예외] {} {} status={} time={}ms error={}", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMillis, exception.getMessage());
	}

	private long getElapsedMillis(HttpServletRequest request) {
		Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
		if (startTime instanceof Long startTimeMillis) {
			return System.currentTimeMillis() - startTimeMillis;
		}
		return 0L;
	}
}
