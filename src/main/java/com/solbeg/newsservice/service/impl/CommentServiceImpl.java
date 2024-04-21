package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseComment;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.Comment;
import com.solbeg.newsservice.exception.AccessDeniedException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.mapper.CommentMapper;
import com.solbeg.newsservice.repository.CommentRepository;
import com.solbeg.newsservice.service.CommentService;
import com.solbeg.newsservice.service.NewsService;
import com.solbeg.newsservice.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserDataService userDataService;
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    private final NewsService newsService;

    @Override
    public ResponseCommentNews getById(UUID id) {
        return commentRepository.findById(id)
                .map(commentMapper::toResponseWithNewsId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + id));
    }

    @Override
    public ResponseNewsWithComments getAllByNewsId(UUID newsId, Pageable pageable) {
        ResponseNews response = newsService.getById(newsId);
        List<ResponseComment> responses = commentMapper.toResponses(commentRepository.findAllByNewsId(newsId, pageable));
        return commentMapper.toNewsWithCommentsResponse(response, responses);
    }

    @Override
    public Page<ResponseCommentNews> getAll(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(commentMapper::toResponseWithNewsId);
    }

    @Override
    public Page<ResponseCommentNews> getAllByFilter(Filter filter, Pageable pageable) {
        return Optional.ofNullable(filter.part())
                .map(part -> "%" + part + "%")
                .map(part -> (Specification<Comment>) (root, query, cb) -> cb.like(root.get("text"), part))
                .map(spec -> commentRepository.findAll(spec, pageable))
                .orElseGet(() -> commentRepository.findAll(pageable))
                .map(commentMapper::toResponseWithNewsId);
    }

    @Override
    @Transactional
    public ResponseCommentNews create(CreateCommentDto commentDto, String token) {
        UserResponse userInDB = userDataService.getUserData(token);
        try {
            return Optional.of(commentDto)
                    .map(commentMapper::toComment)
                    .map(comment -> {
                        comment.setCreatedBy(userInDB.id());
                        comment.setUsername(userInDB.firstName() + " " + userInDB.lastName());
                        return commentRepository.persistAndFlush(comment);
                    })
                    .map(commentMapper::toResponseWithNewsId)
                    .orElseThrow();
        } catch (DataAccessException exception){
            throw  new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + commentDto.newsId());
        }
    }

    @Override
    @Transactional
    public ResponseCommentNews update(UUID id, UpdateCommentDto dto, String token) {
        UserResponse userInDB = getUserResponse(id, token);
        return commentRepository.findById(id)
                .map(current -> {
                    Comment updatedComment = commentMapper.update(dto, current);
                    updatedComment.setUpdatedBy(userInDB.id());
                    return commentRepository.persistAndFlush(updatedComment);
                })
                .map(commentMapper::toResponseWithNewsId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + id));
    }

    @Override
    @Transactional
    public void delete(UUID id, String token) {
        getUserResponse(id, token);
        commentRepository.deleteById(id);
    }

    /**
     * Checks whether the data belongs to the user and returns {@link UserResponse}.
     *
     * @param id    of comment.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link UserResponse} with information about user.
     */
    private UserResponse getUserResponse(UUID id, String token) {
        UserResponse userInDB = userDataService.getUserData(token);
        ResponseCommentNews commentNews = getById(id);
        if (!userInDB.roles().contains("ADMIN") && (!userInDB.roles().contains("SUBSCRIBER") || !userInDB.id().equals(commentNews.createdBy()))) {
            throw new AccessDeniedException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        return userInDB;
    }
}