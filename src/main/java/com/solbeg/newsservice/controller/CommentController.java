package com.solbeg.newsservice.controller;

import com.solbeg.newsservice.controller.openapi.CommentOpenApi;
import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import com.solbeg.newsservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController implements CommentOpenApi {
    private final CommentService commentService;

    @Override
    @GetMapping("/{id}")
    public ResponseCommentNews getCommentById(@Valid @PathVariable UUID id) {
        return commentService.getById(id);
    }

    @Override
    @GetMapping("/news/{newsId}")
    public ResponseNewsWithComments getCommentsByNewsId(@Valid @PathVariable UUID newsId, Pageable pageable) {
        return commentService.getAllByNewsId(newsId, pageable);
    }

    @Override
    @GetMapping
    public Page<ResponseCommentNews> getAllComments(Pageable pageable) {
        return commentService.getAll(pageable);
    }

    @Override
    @GetMapping("/filter")
    public Page<ResponseCommentNews> getAllCommentsByFilter(@Valid @RequestBody Filter filter, Pageable pageable) {
        return commentService.getAllByFilter(filter, pageable);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseCommentNews createComment(@Valid @RequestBody CreateCommentDto commentDto, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return commentService.create(commentDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseCommentNews updateComment(@PathVariable UUID id, @Valid @RequestBody UpdateCommentDto updateCommentDto, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return commentService.update(id, updateCommentDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIBER') || hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCommentById(@PathVariable UUID id, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        commentService.delete(id, token);
    }
}