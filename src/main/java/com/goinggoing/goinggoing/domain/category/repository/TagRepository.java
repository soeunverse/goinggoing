package com.goinggoing.goinggoing.domain.category.repository;

import com.goinggoing.goinggoing.domain.category.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

	List<Tag> findAllByOrderByDisplayOrderAscIdAsc();
}
