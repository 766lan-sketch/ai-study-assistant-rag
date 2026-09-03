package com.example.aifullstack.service;

import java.util.List;

public record KnowledgeChunk(String title, List<String> keywords, String content) {}
