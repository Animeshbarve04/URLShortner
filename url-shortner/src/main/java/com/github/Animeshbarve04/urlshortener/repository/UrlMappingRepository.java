package com.github.Animeshbarve04.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.github.Animeshbarve04.urlshortener.entity.UrlMapping;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

	Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Query(
      value = "SELECT NEXT VALUE FOR url_sequence",
      nativeQuery = true
    )
    Long getNextId();

}