package com.example.aifullstack.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeepSeekClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public DeepSeekClient(
            ObjectMapper objectMapper,
            @Value("${ai.deepseek.api-key:}") String apiKey,
            @Value("${ai.deepseek.api-url:https://api.deepseek.com/chat/completions}") String apiUrl,
            @Value("${ai.deepseek.model:deepseek-v4-flash}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public Optional<String> ask(String question, List<KnowledgeChunk> chunks) {
        if (apiKey == null || apiKey.isBlank()) return Optional.empty();
        try {
            String context = chunks.isEmpty()
                    ? "本地知识库没有检索到相关资料。"
                    : chunks.stream()
                            .map(chunk -> "【" + chunk.title() + "】\n" + chunk.content())
                            .reduce((left, right) -> left + "\n\n" + right)
                            .orElse("");
            Map<String, Object> body = Map.of(
                    "model", model,
                    "thinking", Map.of("type", "disabled"),
                    "stream", false,
                    "messages", List.of(
                            Map.of("role", "system", "content", "你是一名耐心的Java入门老师。请优先依据提供的本地知识库资料，用简短、通俗的中文回答；资料不足时要明确说明，不要编造。"),
                            Map.of("role", "user", "content", "本地知识库资料：\n" + context + "\n\n用户问题：" + question)));
            HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) return Optional.empty();
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText();
            return content.isBlank() ? Optional.empty() : Optional.of(content);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
