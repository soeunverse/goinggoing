package com.goinggoing.goinggoing.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final ApiLoggingInterceptor apiLoggingInterceptor;
	private final String[] allowedOrigins;

	public WebMvcConfig(
			ApiLoggingInterceptor apiLoggingInterceptor,
			@Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8081,http://localhost:19006}") String allowedOrigins
	) {
		this.apiLoggingInterceptor = apiLoggingInterceptor;
		this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(origin -> !origin.isBlank())
				.toArray(String[]::new);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(apiLoggingInterceptor)
				.addPathPatterns("/api/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigins)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.exposedHeaders("Authorization")
				.allowCredentials(true)
				.maxAge(3600);
	}
}
