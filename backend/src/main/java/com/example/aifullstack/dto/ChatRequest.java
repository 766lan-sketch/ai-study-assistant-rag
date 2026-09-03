package com.example.aifullstack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(@NotBlank(message = "问题不能为空") @Size(max = 500) String question) {}
