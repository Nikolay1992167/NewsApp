package com.solbeg.newsservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseComment(UUID id,
                              UUID createdBy,
                              UUID updatedBy,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              String text,
                              String username) {
}