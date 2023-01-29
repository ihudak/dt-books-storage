package com.dynatrace.storage.repository;

import com.dynatrace.storage.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    Storage findByIsbn(String isbn);
}
