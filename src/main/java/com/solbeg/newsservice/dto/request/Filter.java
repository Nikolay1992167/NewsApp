package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Filter(

        @NotBlank
        @Size(min = 1, max = 50)
        String part) {
}