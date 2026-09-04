package com.example.aifullstack.service;

import com.example.aifullstack.dto.RagCaseResult;
import com.example.aifullstack.dto.RagEvaluationResult;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 使用固定问题集对检索层做可重复的离线评测。
 * 评测不调用大模型，因此不会消耗 API 额度，结果也不会受模型随机性影响。
 */
@Service
public class RagEvaluationService {
    private static final List<EvaluationCase> CASES = List.of(
            new EvaluationCase("Controller 怎样接收 HTTP 请求？", "Controller"),
            new EvaluationCase("业务逻辑应该放在哪一层？", "Service"),
            new EvaluationCase("Spring Data JPA 如何保存数据库记录？", "Repository"),
            new EvaluationCase("Spring Boot 为什么可以快速创建 Web 项目？", "Spring Boot"),
            new EvaluationCase("Java 中类和对象有什么区别？", "Java 类与对象"),
            new EvaluationCase("GET、POST、PUT、DELETE 分别有什么作用？", "RESTful API"),
            new EvaluationCase("pom.xml 如何管理项目依赖？", "Maven"));

    private final RagService ragService;

    public RagEvaluationService(RagService ragService) {
        this.ragService = ragService;
    }

    public RagEvaluationResult evaluate() {
        List<RagCaseResult> results = CASES.stream().map(this::evaluateCase).toList();
        long hits = results.stream().filter(RagCaseResult::hit).count();
        double accuracy = results.isEmpty() ? 0 : Math.round(hits * 1000.0 / results.size()) / 10.0;
        return new RagEvaluationResult(results.size(), hits, accuracy, results);
    }

    private RagCaseResult evaluateCase(EvaluationCase evaluationCase) {
        String retrievedTitle = ragService.retrieve(evaluationCase.question()).stream()
                .findFirst().map(KnowledgeChunk::title).orElse("");
        boolean hit = retrievedTitle.contains(evaluationCase.expectedTitle());
        return new RagCaseResult(
                evaluationCase.question(), evaluationCase.expectedTitle(), retrievedTitle, hit);
    }

    private record EvaluationCase(String question, String expectedTitle) {}
}
