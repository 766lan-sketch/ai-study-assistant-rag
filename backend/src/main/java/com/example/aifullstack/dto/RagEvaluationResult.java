package com.example.aifullstack.dto;

import java.util.List;

/** RAG 检索层的离线 Top-1 命中率报告。 */
public record RagEvaluationResult(
        int totalCases,
        long top1Hits,
        double top1Accuracy,
        List<RagCaseResult> cases) {}
