package com.solbeg.newsservice.controller;

import com.solbeg.newsservice.controller.openapi.NewsOpenApi;
import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController implements NewsOpenApi {
    private final NewsService newsService;

    @Override
    @GetMapping("/{id}")
    public ResponseNews getNewsById(@Valid @PathVariable UUID id) {
        return newsService.getById(id);
    }

    @Override
    @GetMapping
    public Page<ResponseNews> getAllNews(@PageableDefault(20) Pageable pageable) {
        return newsService.getAll(pageable);
    }

    @Override
    @GetMapping("/filter")
    public Page<ResponseNews> getAllNewsByFilter(@Valid @RequestBody Filter filter, @PageableDefault(20) Pageable pageable) {
        return newsService.getAllByFilter(filter, pageable);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST') || hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseNews createNews(@Valid @RequestBody CreateNewsDto createNewsDto,
                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return newsService.create(createNewsDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST') || hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseNews updateNews(@PathVariable UUID id,
                                   @Valid @RequestBody CreateNewsDto createNewsDto,
                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return newsService.update(id, createNewsDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST') || hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteNewsById(@PathVariable UUID id,
                               @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        newsService.delete(id, token);
    }
}