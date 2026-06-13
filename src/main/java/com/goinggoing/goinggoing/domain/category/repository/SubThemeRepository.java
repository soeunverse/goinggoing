package com.goinggoing.goinggoing.domain.category.repository;

import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubThemeRepository extends JpaRepository<SubTheme, Long> {

	List<SubTheme> findByThemeIdOrderByDisplayOrderAscIdAsc(Long themeId);
}
