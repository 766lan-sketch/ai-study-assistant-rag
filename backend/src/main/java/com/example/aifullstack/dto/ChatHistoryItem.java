package com.example.aifullstack.dto;

import java.time.Instant;
import java.util.List;

public record ChatHistoryItem(Long id, String question, String answer, Instant createdAt, List<KnowledgeSource> sources) {}
