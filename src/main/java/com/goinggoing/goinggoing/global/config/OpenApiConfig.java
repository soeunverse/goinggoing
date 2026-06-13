package com.goinggoing.goinggoing.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI goinggoingOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("GoingGoing API")
						.description("당일치기 및 1박 2일 여행 컨텐츠 추천 서비스 API")
						.version("v1")
						.license(new License().name("GoingGoing MVP")));
	}
}
