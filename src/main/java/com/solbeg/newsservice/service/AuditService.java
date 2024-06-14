package com.solbeg.newsservice.service;

import com.solbeg.newsservice.dto.response.HistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface AuditService<T> {

    Page<HistoryResponse> findHistoryForAllTime(UUID id, Pageable pageable);

    Page<HistoryResponse> findHistoryForPeriodFromStartDateToEndDate(UUID id,
                                                                     LocalDateTime startDate,
                                                                     LocalDateTime endDate,
                                                                     Pageable pageable);

    default Page<HistoryResponse> findHistoryInPeriod(UUID id,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate,
                                                      Pageable pageable,
                                                      RevisionRepository<T, UUID, Long> repository) {

        List<HistoryResponse> historyResponses = new ArrayList<>();
        Page<Revision<Long, T>> revisions = repository.findRevisions(id, pageable);

        revisions.getContent().stream()
                .filter(revision -> isWithinPeriod(revision, startDate, endDate))
                .forEach(revision -> {
                    String changeType = getChangeType(revision);
                    setDataInListResponse(id, revision, historyResponses, changeType);
                });
        return new PageImpl<>(historyResponses);
    }

    default boolean isWithinPeriod(Revision<Long, T> revision, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return true;
        }
        LocalDateTime revisionTime = LocalDateTime.ofInstant(revision.getRevisionInstant().get(), ZoneId.systemDefault());
        return revisionTime.isAfter(startDate) && revisionTime.isBefore(endDate);
    }

    default String getChangeType(Revision<Long, T> revision) {
        return switch (revision.getMetadata().getRevisionType()) {
            case UNKNOWN -> null;
            case INSERT -> "created";
            case UPDATE -> "changed";
            case DELETE -> "deleted";
        };
    }

    default void setDataInListResponse(UUID id,
                                       Revision<Long, T> revision,
                                       List<HistoryResponse> historyResponses,
                                       String changeType) {
        historyResponses.add(HistoryResponse.builder()
                .id(id)
                .createdAt(LocalDateTime.ofInstant(revision.getRevisionInstant().get(), ZoneId.systemDefault()))
                .historyMessage("The comment has been " + changeType + ": " + revision.getEntity())
                .build());
    }
}