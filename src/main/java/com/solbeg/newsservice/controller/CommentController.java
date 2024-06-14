package com.solbeg.newsservice.controller;

import com.solbeg.newsservice.controller.openapi.CommentOpenApi;
import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import com.solbeg.newsservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController implements CommentOpenApi {
    private final CommentService commentService;

    @Override
    @GetMapping("/{commentId}")
    public ResponseCommentNews findCommentById(@PathVariable UUID commentId) {
        return commentService.findCommentById(commentId);
    }

    @Override
    @GetMapping("/news/{newsId}")
    public ResponseNewsWithComments findCommentsByNewsId(@PathVariable UUID newsId, Pageable pageable) {
        return commentService.findCommentsByNewsId(newsId, pageable);
    }

    @Override
    @GetMapping
    public Page<ResponseCommentNews> getAllComments(Pageable pageable) {
        return commentService.getAllComments(pageable);
    }

    @Override
    @GetMapping("/filter")
    public Page<ResponseCommentNews> findCommentsByFilter(@Validated @RequestBody Filter filter, Pageable pageable) {
        return commentService.findCommentsByFilter(filter, pageable);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseCommentNews createComment(@Validated @RequestBody CreateCommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @PutMapping("/{commentId}")
    public ResponseCommentNews updateComment(@PathVariable UUID commentId, @Validated @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateCommentById(commentId, updateCommentDto);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @DeleteMapping("/{commentId}")
    public void deleteCommentById(@PathVariable UUID commentId) {
        commentService.deleteCommentById(commentId);
    }
}