package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.exception.AccessDeniedException;
import com.solbeg.newsservice.exception.CreateObjectException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.mapper.NewsMapper;
import com.solbeg.newsservice.repository.NewsRepository;
import com.solbeg.newsservice.service.NewsService;
import com.solbeg.newsservice.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final UserDataService userDataService;
    private final NewsMapper newsMapper = Mappers.getMapper(NewsMapper.class);

    @Override
    public ResponseNews getById(UUID id) {
        return newsRepository.findById(id)
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + id));
    }

    @Override
    public Page<ResponseNews> getAll(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(newsMapper::toResponseNews);
    }

    @Override
    public Page<ResponseNews> getAllByFilter(Filter filter, Pageable pageable) {
        return Optional.ofNullable(filter.part())
                .map(part -> "%" + part + "%")
                .map(part -> Specification.anyOf(
                        (Specification<News>) (root, query, cb) -> cb.like(root.get("text"), part),
                        (Specification<News>) (root, query, cb) -> cb.like(root.get("title"), part)))
                .map(spec -> newsRepository.findAll(spec, pageable))
                .orElseGet(() -> newsRepository.findAll(pageable))
                .map(newsMapper::toResponseNews);
    }

    @Override
    @Transactional
    public ResponseNews create(CreateNewsDto createNewsDto, String token) {
        validationAuthorDetails(createNewsDto.idAuthor(), token);
        UserResponse userInDB = userDataService.getUserData(token);
        return Optional.of(createNewsDto)
                .map(newsMapper::toNews)
                .map(comment -> {
                    comment.setCreatedBy(userInDB.id());
                    return newsRepository.persistAndFlush(comment);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new CreateObjectException(ErrorMessage.ERROR_CREATE_OBJECT.getMessage()));
    }

    @Override
    @Transactional
    public ResponseNews update(UUID id, CreateNewsDto createNewsDto, String token) {
        UserResponse userInDB = getUserResponse(id, token);
        return newsRepository.findById(id)
                .map(current -> {
                    News updateNews = newsMapper.merge(current, createNewsDto);
                    updateNews.setUpdatedBy(userInDB.id());
                    return newsRepository.persistAndFlush(updateNews);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + id));
    }

    @Override
    @Transactional
    public void delete(UUID id, String token) {
        getUserResponse(id, token);
        newsRepository.deleteById(id);
    }

    /**
     * Checks whether the data belongs to the user and returns {@link UserResponse}.
     *
     * @param id    of news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link UserResponse} with information about user.
     */
    private UserResponse getUserResponse(UUID id, String token) {
        UserResponse userInDB = userDataService.getUserData(token);
        ResponseNews news = getById(id);
        if (!userInDB.roles().contains("ADMIN") && (!userInDB.roles().contains("JOURNALIST") || !userInDB.id().equals(news.createdBy()))) {
            throw new AccessDeniedException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        return userInDB;
    }

    private Boolean validationAuthorDetails(UUID idAuthor, String token) {
        if (userDataService.getUserData(idAuthor, token) == null) {
            throw new NotFoundException("User with this id not registered!");
        };
        return true;
    }
}