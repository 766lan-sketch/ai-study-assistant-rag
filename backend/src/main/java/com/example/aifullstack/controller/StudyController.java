package com.example.aifullstack.controller;

import com.example.aifullstack.dto.ChatRequest;
import com.example.aifullstack.dto.ChatResponse;
import com.example.aifullstack.dto.ChatHistoryItem;
import com.example.aifullstack.dto.StudyTask;
import com.example.aifullstack.service.StudyAssistantService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")
//所有学习接口共同的开头
@CrossOrigin(origins = "http://localhost:5173")
public class StudyController {
    private final StudyAssistantService service;

    public StudyController(StudyAssistantService service) {
        this.service = service;
    }

    @GetMapping("/tasks")
//    它负责获取左侧的“今日任务”
    public List<StudyTask> tasks() {
        return service.todayTasks();
    }

    @PostMapping("/chat")
//    你在网页点击“发送”时，请求就会来到下面的 chat() 方法，
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
}
