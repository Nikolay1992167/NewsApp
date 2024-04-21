package com.solbeg.newsservice.repository;

import com.solbeg.newsservice.entity.News;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface NewsRepository extends BaseJpaRepository<News, UUID> {
    Page<News> findAll(Pageable pageable);

    Page<News> findAll(Specification<News> specification, Pageable pageable);
}