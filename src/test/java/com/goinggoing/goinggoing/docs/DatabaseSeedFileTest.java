package com.goinggoing.goinggoing.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseSeedFileTest {

	@Test
	@DisplayName("MVP seed SQL은 지역, 테마, 하위 카테고리, 태그 데이터를 포함한다")
	void seedFileContainsMvpCategoryData() throws Exception {
		String seedSql = Files.readString(Path.of("docs/database/seed-mvp.sql"));

		assertThat(seedSql).contains("INSERT INTO regions");
		assertThat(seedSql).contains("서울특별시", "부산광역시", "대전광역시", "제주특별자치도");
		assertThat(seedSql).contains("INSERT INTO themes");
		assertThat(seedSql).contains("INSERT INTO sub_themes");
		assertThat(seedSql).contains("INSERT INTO tags");
	}
}
