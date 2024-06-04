package com.jpa.demo.repository;

import com.jpa.demo.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
}
