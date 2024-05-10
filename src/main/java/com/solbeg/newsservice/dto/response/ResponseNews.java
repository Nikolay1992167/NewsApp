package com.solbeg.newsservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ResponseNews(UUID id,
                           UUID createdBy,
                           UUID updatedBy,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           String title,
                           String text,
                           UUID idAuthor) {
}
