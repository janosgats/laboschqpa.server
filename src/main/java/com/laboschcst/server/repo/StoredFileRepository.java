package com.laboschcst.server.repo;

import com.laboschcst.server.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
}