package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.solbeg.newsservice.util.Constants.SIZE_FROM_1_TO_50_CHARACTERS;

@Builder
public record Filter(

        @NotBlank
        @Size(min = 1, max = 50, message = SIZE_FROM_1_TO_50_CHARACTERS)
        String part) {
}