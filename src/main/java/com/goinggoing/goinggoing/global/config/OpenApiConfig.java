package com.goinggoing.goinggoing.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI goinggoingOpenApi() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("로그인 API에서 받은 accessToken을 입력하세요.")))
				.info(new Info()
						.title("GoingGoing API")
						.description("당일치기 및 1박 2일 여행 컨텐츠 추천 서비스 API")
						.version("v1")
						.license(new License().name("GoingGoing MVP")));
	}
}
