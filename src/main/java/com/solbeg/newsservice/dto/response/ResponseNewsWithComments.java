package com.solbeg.newsservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Builder
public record ResponseNewsWithComments(UUID id,
                                       UUID createdBy,
                                       UUID updatedBy,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt,
                                       String title,
                                       String text,
                                       UUID idAuthor,
                                       List<ResponseComment> comments) {
}
