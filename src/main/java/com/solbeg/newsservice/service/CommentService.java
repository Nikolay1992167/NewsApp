package com.solbeg.newsservice.service;

import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseComment;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import com.solbeg.newsservice.entity.Comment;
import com.solbeg.newsservice.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    /**
     * Searches for the saved {@link Comment} and returns {@link ResponseCommentNews} with information about the comment.
     *
     * @param id of comment.
     * @return {@link ResponseCommentNews} with information about {@link Comment}.
     */
    ResponseCommentNews getById(UUID id);

    /**
     * Searches for all {@link Comment} by the news ID and
     * returns a page with {@link ResponseComment} with information about the comment, with the specified pagination parameters.
     *
     * @param newsId id of {@link News}.
     * @param pageable object {@link Pageable} containing information about the page number and size.
     * @return found {@link Page} with {@link ResponseComment} with information about the comment.
     */
    ResponseNewsWithComments getAllByNewsId(UUID newsId, Pageable pageable);

    /**
     * Method for getting the {@link ResponseComment} page with information about the comment
     * with the specified pagination parameters.
     *
     * @param pageable object {@link Pageable} containing information about the page number and size.
     * @return object {@link Page} with {@link ResponseComment} with information about the comment.
     */
    Page<ResponseCommentNews> getAll(Pageable pageable);

    /**
     * Returns {@link Page} containing {@link ResponseCommentNews} objects
     * with preset pagination and filtering parameters.
     *
     * @param filter the {@link Filter} object containing the filtering criteria.
     * @param pageable object {@link Pageable} containing information about the page number and size.
     * @return object {@link Page} containing objects {@link ResponseCommentNews} with information about the news.
     */
    Page<ResponseCommentNews> getAllByFilter(Filter filter, Pageable pageable);

    /**
     * Creates a new {@link Comment} based on the specified {@link CreateCommentDto} object
     * and returns {@link ResponseCommentNews} with information about the created comment.
     *
     * @param dto object {@link CreateCommentDto} containing data for creating news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseCommentNews} with information about {@link Comment}.
     */
    ResponseCommentNews create(CreateCommentDto dto, String token);

    /**
     * Updates an existing {@link Comment} using data from {@link UpdateCommentDto}.
     *
     * @param id of comment.
     * @param dto object {@link UpdateCommentDto} containing data for updating the comment.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseCommentNews} with information about the updated {@link Comment}.
     */
    ResponseCommentNews update(UUID id, UpdateCommentDto dto, String token);

    /**
     * Deletes {@link Comment} by ID.
     *
     * @param id of comment.
     * @param token a string containing the authentication token in the request header.
     */
    void delete(UUID id, String token);
}