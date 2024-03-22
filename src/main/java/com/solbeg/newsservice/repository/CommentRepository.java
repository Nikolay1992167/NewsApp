package com.solbeg.newsservice.repository;

import com.solbeg.newsservice.entity.Comment;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.UUID;

public interface CommentRepository extends BaseJpaRepository<Comment, UUID> {
}
