package com.solbeg.newsservice.repository;

import com.solbeg.newsservice.entity.Comment;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends BaseJpaRepository<Comment, UUID> {
    List<Comment> findAllByNewsId(UUID newsId, Pageable pageable);

    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findAll(Specification<Comment> specification, Pageable pageable);
}