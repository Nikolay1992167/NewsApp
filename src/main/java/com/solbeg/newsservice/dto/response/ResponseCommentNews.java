package com.solbeg.newsservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseCommentNews(UUID id,
                                  UUID createdBy,
                                  UUID updatedBy,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt,
                                  String text,
                                  String username,
                                  UUID newsId) {
}