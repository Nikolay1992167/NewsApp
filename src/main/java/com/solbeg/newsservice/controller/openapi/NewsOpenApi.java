package com.solbeg.newsservice.controller.openapi;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.response.ResponseNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface NewsOpenApi {

    ResponseEntity<ResponseNews> getNewsById(UUID id);

    ResponseEntity<Page<ResponseNews>> getAllNews(Pageable pageable);

    ResponseEntity<Page<ResponseNews>> getAllNewsByFilter(Filter filter, Pageable pageable);


    ResponseEntity<ResponseNews> createNews(CreateNewsDto createNewsDto, String token);

    ResponseEntity<ResponseNews> updateNews(UUID id, CreateNewsDto createNewsDto, String token);

    ResponseEntity<Void> deleteNewsById(UUID id, String token);
}
