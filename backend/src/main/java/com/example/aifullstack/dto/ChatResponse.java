package com.example.aifullstack.dto;

import java.time.Instant;
import java.util.List;

public record ChatResponse(String answer, Instant createdAt, List<KnowledgeSource> sources) {}
