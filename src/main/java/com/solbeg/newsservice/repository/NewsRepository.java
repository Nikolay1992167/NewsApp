package com.solbeg.newsservice.repository;

import com.solbeg.newsservice.entity.News;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.UUID;

public interface NewsRepository extends BaseJpaRepository<News, UUID> {
}
