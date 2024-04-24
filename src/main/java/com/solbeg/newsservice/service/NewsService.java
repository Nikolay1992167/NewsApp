package com.solbeg.newsservice.service;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NewsService {

    /**
     * Searches for the saved {@link News} and returns {@link ResponseNews} with information about the news.
     *
     * @param id of news.
     * @return {@link ResponseNews} with information about {@link News}.
     */
    ResponseNews getById(UUID id);

    /**
     * Method for getting the {@link ResponseNews} page with information about the news
     * with the specified pagination parameters.
     *
     * @param pageable object {@link Pageable} containing information about the page number and size.
     * @return object {@link Page} with {@link ResponseNews} with information about the news.
     */
    Page<ResponseNews> getAll(Pageable pageable);

    /**
     * Returns {@link Page} containing {@link ResponseNews} objects
     * with preset pagination and filtering parameters.
     *
     * @param filter   the {@link Filter} object containing the filtering criteria.
     * @param pageable object {@link Pageable} containing information about the page number and size.
     * @return object {@link Page} containing objects {@link ResponseNews} with information about the news.
     */
    Page<ResponseNews> getAllByFilter(Filter filter, Pageable pageable);

    /**
     * Creates a new {@link News} for user with role 'ADMIN' created based on the specified {@link CreateNewsDto} object
     * and returns {@link ResponseNews} with information about the news.
     *
     * @param dto   object {@link CreateNewsDto} containing data for creating news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseNews} with information about {@link News}.
     */
    ResponseNews createNewsAdmin(CreateNewsDto dto, String token);

    /**
     * Creates a new {@link News} for user with role 'JOURNALIST' created based on the specified {@link CreateNewsDto} object
     * and returns {@link ResponseNews} with information about the news.
     *
     * @param dto   object {@link CreateNewsDto} containing data for creating news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseNews} with information about {@link News}.
     */
    ResponseNews createNewsJournalist(CreateNewsDtoJournalist dto, String token);

    /**
     * Updates an existing {@link News} using data from {@link CreateNewsDto} for user with role 'ADMIN'.
     *
     * @param id of news.
     * @param dto object {@link CreateNewsDto} containing data for updating the news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseNews} with information about the updated {@link News}.
     */
    ResponseNews updateAdmin(UUID id, CreateNewsDto dto, String token);

    /**
     * Updates an existing {@link News} using data from {@link CreateNewsDtoJournalist} for user with role 'JOURNALIST'.
     *
     * @param id  of news.
     * @param dto object {@link CreateNewsDtoJournalist} containing data for updating the news.
     * @param token a string containing the authentication token in the request header.
     * @return object {@link ResponseNews} with information about the updated {@link News}.
     */
    ResponseNews updateJournalist(UUID id, CreateNewsDtoJournalist dto, String token);

    /**
     * Deletes {@link News} by id.
     *
     * @param id    of news.
     * @param token a string containing the authentication token in the request header.
     */
    void delete(UUID id, String token);
}