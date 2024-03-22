package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentDto(

        @NotBlank
        @Size(min = 3, max = 500)
        String text,

        @NotNull
        Long newsId) {
}