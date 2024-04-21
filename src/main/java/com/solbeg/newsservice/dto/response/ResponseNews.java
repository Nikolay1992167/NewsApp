package com.solbeg.newsservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseNews(UUID id,
                           UUID createdBy,
                           UUID updatedBy,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           String title,
                           String text,
                           UUID idAuthor) {
}
