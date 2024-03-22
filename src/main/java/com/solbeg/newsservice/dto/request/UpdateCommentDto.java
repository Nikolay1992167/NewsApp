package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentDto(

        @NotBlank
        String text) {
}