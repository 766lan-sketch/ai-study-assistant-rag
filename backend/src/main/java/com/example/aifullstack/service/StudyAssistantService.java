 package com.example.aifullstack.service;

import com.example.aifullstack.dto.ChatResponse;
import com.example.aifullstack.dto.ChatHistoryItem;
import com.example.aifullstack.dto.StudyTask;
import com.example.aifullstack.dto.KnowledgeSource;
import com.example.aifullstack.entity.ChatMessage;
import com.example.aifullstack.repository.ChatMessageRepository;
import java.time.Instant;
import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class StudyAssistantService {
    private final ChatMessageRepository repository;
    private final DeepSeekClient deepSeekClient;
    private final RagService ragService;

    public StudyAssistantService(ChatMessageRepository repository, DeepSeekClient deepSeekClient, RagService ragService) {
        this.repository = repository;
        this.deepSeekClient = deepSeekClient;
        this.ragService = ragService;
    }

    public List<StudyTask> todayTasks() {
        return List.of(
                new StudyTask(1, "认识 Controller", "理解浏览器请求如何进入 Java 方法", false),
                new StudyTask(2, "认识 Service", "把业务逻辑从接口层拆分出来", false),
                new StudyTask(3, "完成一次联调", "从 Vue 页面发送问题并显示后端回答", false));
    }

    public ChatResponse answer(String question) {
        String text = question.trim();
        List<KnowledgeChunk> chunks = ragService.retrieve(text);
        List<KnowledgeSource> sources = chunks.stream()
                .map(chunk -> new KnowledgeSource(chunk.title(), chunk.content()))
                .toList();
        String answer = deepSeekClient.ask(text, chunks)
                .orElseGet(() -> ragService.fallbackAnswer(text, chunks));
        Instant createdAt = Instant.now();
        String sourceTitles = sources.stream().map(KnowledgeSource::title).reduce((a, b) -> a + "|||" + b).orElse("");
        repository.save(new ChatMessage(text, answer, sourceTitles, createdAt));
//        它负责把新对话保存进数据库。
        return new ChatResponse(answer, createdAt, sources);
    }

    public List<ChatHistoryItem> history() {
        return repository.findTop20ByOrderByCreatedAtAsc().stream()
                .map(item -> new ChatHistoryItem(item.getId(), item.getQuestion(), item.getAnswer(), item.getCreatedAt(), parseSources(item.getSources())))
                .toList();
    }

    public void clearHistory() {
        repository.deleteAll();
    }

    private List<KnowledgeSource> parseSources(String sourceTitles) {
        if (sourceTitles == null || sourceTitles.isBlank()) return List.of();
        return Arrays.stream(sourceTitles.split("\\|\\|\\|"))
                .map(title -> new KnowledgeSource(title, "历史记录中的知识库来源"))
                .toList();
    }
}
