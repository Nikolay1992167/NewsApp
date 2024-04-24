package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.exception.AccessException;
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
    public ResponseNews createNewsAdmin(CreateNewsDto createNewsDto, String token) {
        UserResponse userInDB = userDataService.getUserData(createNewsDto.idAuthor(), token);
        return Optional.of(createNewsDto)
                .map(newsMapper::toNews)
                .map(news -> {
                    news.setCreatedBy(userInDB.id());
                    return newsRepository.persistAndFlush(news);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new CreateObjectException(ErrorMessage.ERROR_CREATE_OBJECT.getMessage()));
    }

    @Override
    @Transactional
    public ResponseNews createNewsJournalist(CreateNewsDtoJournalist createNewsDtoJournalist, String token) {
        UUID userId = getId(token);
        return Optional.of(createNewsDtoJournalist)
                .map(newsMapper::toNews)
                .map(news -> {
                    news.setCreatedBy(userId);
                    news.setIdAuthor(userId);
                    return newsRepository.persistAndFlush(news);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new CreateObjectException(ErrorMessage.ERROR_CREATE_OBJECT.getMessage()));
    }

    @Override
    @Transactional
    public ResponseNews updateAdmin(UUID id, CreateNewsDto createNewsDto, String token) {
        return newsRepository.findById(id)
                .map(current -> {
                    News updateNews = newsMapper.merge(current, createNewsDto);
                    updateNews.setUpdatedBy(getId(token));
                    return newsRepository.persistAndFlush(updateNews);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + id));
    }

    @Override
    @Transactional
    public ResponseNews updateJournalist(UUID id, CreateNewsDtoJournalist createNewsDtoJournalist, String token) {
        UserResponse userInDB = getAuthor(id, token);
        return newsRepository.findById(id)
                .map(current -> {
                    News updateNews = newsMapper.merge(current, createNewsDtoJournalist);
                    updateNews.setUpdatedBy(userInDB.id());
                    return newsRepository.persistAndFlush(updateNews);
                })
                .map(newsMapper::toResponseNews)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + id));
    }

    @Override
    @Transactional
    public void delete(UUID id, String token) {
        if (isAuthor(id, token)) {
            throw new AccessException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        newsRepository.deleteById(id);
    }

    private UUID getId(String token) {
        return userDataService.getUserData(token).id();
    }

    /**
     * Checks whether the data belongs to the user and returns {@link UserResponse}.
     *
     * @param id    of news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link UserResponse} with information about user.
     */
    private UserResponse getAuthor(UUID id, String token) {
        UserResponse userInDB = userDataService.getUserData(token);
        ResponseNews news = getById(id);
        if (!userInDB.id().equals(news.createdBy())) {
            throw new AccessException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        return userInDB;
    }

    private Boolean isAuthor(UUID id, String token) {
        UserResponse userInDB = userDataService.getUserData(token);
        ResponseNews news = getById(id);
        return userInDB.roles().contains("JOURNALIST") && !userInDB.id().equals(news.createdBy());
    }
}