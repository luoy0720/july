package com.example.collector.dto;

import com.example.collector.model.TaskSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(
        @NotBlank String name,
        @NotNull TaskSourceType sourceType,
        @NotBlank String cronExpr,
        boolean enabled,
        String configJson
) {
}
