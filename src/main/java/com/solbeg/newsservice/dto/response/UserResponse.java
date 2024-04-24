package com.solbeg.newsservice.dto.response;

import com.solbeg.newsservice.enams.Status;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        UUID createdBy,
        UUID updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String firstName,
        String lastName,
        String password,
        String email,
        List<String> roles,
        Status status
) {
}