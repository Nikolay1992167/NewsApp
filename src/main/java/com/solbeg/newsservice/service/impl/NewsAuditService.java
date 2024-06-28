package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.response.HistoryResponse;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.repository.NewsRepository;
import com.solbeg.newsservice.service.AuditService;
import com.solbeg.newsservice.validation.NewsValidator;
import com.solbeg.newsservice.validation.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsAuditService implements AuditService<News> {
    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;

    @Transactional(readOnly = true)
    public Page<HistoryResponse> findHistoryForAllTime(UUID newsId, Pageable pageable) {
        newsValidator.validateExists(newsId);
        return findHistoryInPeriod(newsId, null, null, pageable, newsRepository);
    }

    @Transactional(readOnly = true)
    public Page<HistoryResponse> findHistoryForPeriodFromStartDateToEndDate(UUID newsId,
                                                                            LocalDateTime startDate,
                                                                            LocalDateTime endDate,
                                                                            Pageable pageable) {
        newsValidator.validateExists(newsId);
        TimeValidator.validateTimePeriod(startDate, endDate);
        return findHistoryInPeriod(newsId, startDate, endDate, pageable, newsRepository);
    }
}