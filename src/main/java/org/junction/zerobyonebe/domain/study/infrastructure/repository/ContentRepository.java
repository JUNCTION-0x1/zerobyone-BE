package org.junction.zerobyonebe.domain.study.infrastructure.repository;

import org.junction.zerobyonebe.domain.study.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
