package com.example.aifullstack.dto;

/** 单条离线检索评测结果。 */
public record RagCaseResult(
        String question,
        String expectedTitle,
        String retrievedTitle,
        boolean hit) {}
