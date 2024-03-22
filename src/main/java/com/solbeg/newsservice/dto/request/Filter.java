package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.Size;

public record Filter(@Size(min = 1) String part) {
}