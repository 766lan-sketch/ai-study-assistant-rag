package com.example.aifullstack.controller;

import com.example.aifullstack.dto.ChatRequest;
import com.example.aifullstack.dto.ChatResponse;
import com.example.aifullstack.dto.ChatHistoryItem;
import com.example.aifullstack.dto.StudyTask;
import com.example.aifullstack.dto.RagEvaluationResult;
import com.example.aifullstack.service.RagEvaluationService;
import com.example.aifullstack.service.StudyAssistantService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyController {
    private final StudyAssistantService service;
    private final RagEvaluationService evaluationService;

    public StudyController(StudyAssistantService service, RagEvaluationService evaluationService) {
        this.service = service;
        this.evaluationService = evaluationService;
    }

    @GetMapping("/tasks")
    public List<StudyTask> tasks() {
        return service.todayTasks();
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return service.answer(request.question());
    }

    @GetMapping("/history")
    public List<ChatHistoryItem> history() {
        return service.history();
    }

    @DeleteMapping("/history")
    public void clearHistory() {
        service.clearHistory();
    }

    @GetMapping("/rag/evaluation")
    public RagEvaluationResult evaluateRetrieval() {
        return evaluationService.evaluate();
    }
}
