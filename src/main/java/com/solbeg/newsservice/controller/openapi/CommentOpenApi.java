package com.solbeg.newsservice.controller.openapi;

import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface CommentOpenApi {

    ResponseEntity<ResponseCommentNews> getCommentById(UUID id);

    ResponseEntity<ResponseNewsWithComments> getCommentsByNewsId(UUID newsId, Pageable pageable);

    ResponseEntity<Page<ResponseCommentNews>> getAllComments(Pageable pageable);

    ResponseEntity<Page<ResponseCommentNews>> getAllCommentsByFilter(Filter filter, Pageable pageable);

    ResponseEntity<ResponseCommentNews> createComment(CreateCommentDto commentDto, String token);

    ResponseEntity<ResponseCommentNews> updateComment(UUID id, UpdateCommentDto updateCommentDto, String token);

    ResponseEntity<Void> deleteCommentById(UUID id, String token);
}