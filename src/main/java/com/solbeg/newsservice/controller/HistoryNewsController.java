package com.solbeg.newsservice.controller;

import com.solbeg.newsservice.controller.openapi.HistoryNewsOpenApi;
import com.solbeg.newsservice.dto.request.TimePeriod;
import com.solbeg.newsservice.dto.response.HistoryResponse;
import com.solbeg.newsservice.service.impl.NewsAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/history/news")
public class HistoryNewsController implements HistoryNewsOpenApi {
    private final NewsAuditService newsAuditService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{newsId}")
    public Page<HistoryResponse> findHistoryOfNews(@PathVariable UUID newsId, Pageable pageable) {
        return newsAuditService.findHistoryForAllTime(newsId, pageable);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/period/{newsId}")
    public Page<HistoryResponse> findHistoryOfCommentForTimePeriod(@PathVariable UUID newsId,
                                                                   @Validated @RequestBody TimePeriod timePeriod,
                                                                   Pageable pageable) {
        return newsAuditService.findHistoryForPeriodFromStartDateToEndDate(newsId,
                timePeriod.startDate(),
                timePeriod.endDate(),
                pageable);
    }
}