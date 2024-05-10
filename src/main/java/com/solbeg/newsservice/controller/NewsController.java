package com.solbeg.newsservice.controller;

import com.solbeg.newsservice.controller.openapi.NewsOpenApi;
import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
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
    @GetMapping("/{newsId}")
    public ResponseNews findNewsById(@Valid @PathVariable UUID newsId) {
        return newsService.findNewsById(newsId);
    }

    @Override
    @GetMapping
    public Page<ResponseNews> getAllNews(@PageableDefault(20) Pageable pageable) {
        return newsService.getAllNews(pageable);
    }

    @Override
    @GetMapping("/filter")
    public Page<ResponseNews> findNewsByFilter(@Valid @RequestBody Filter filter, @PageableDefault(20) Pageable pageable) {
        return newsService.findNewsByFilter(filter, pageable);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin")
    public ResponseNews createNewsAdmin(@Valid @RequestBody CreateNewsDto createNewsDto,
                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return newsService.createNewsAdmin(createNewsDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/journalist")
    public ResponseNews createNewsJournalist(@Valid @RequestBody CreateNewsDtoJournalist createNewsDtoJournalist) {
        return newsService.createNewsJournalist(createNewsDtoJournalist);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/{newsId}")
    public ResponseNews updateNewsAdmin(@PathVariable UUID newsId,
                                        @Valid @RequestBody CreateNewsDto createNewsDto,
                                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token) {
        return newsService.updateNewsAdmin(newsId, createNewsDto, token);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST')")
    @PutMapping("/journalist/{newsId}")
    public ResponseNews updateNewsJournalist(@PathVariable UUID newsId,
                                        @Valid @RequestBody CreateNewsDtoJournalist createNewsDtoJournalist) {
        return newsService.updateNewsJournalist(newsId, createNewsDtoJournalist);
    }

    @Override
    @PreAuthorize("hasAuthority('JOURNALIST') || hasAuthority('ADMIN')")
    @DeleteMapping("/{newsId}")
    public void deleteNewsById(@PathVariable UUID newsId) {
        newsService.deleteNews(newsId);
    }
}