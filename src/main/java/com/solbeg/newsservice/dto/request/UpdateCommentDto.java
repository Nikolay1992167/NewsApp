package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateCommentDto(

        @NotBlank
        String text) {
}