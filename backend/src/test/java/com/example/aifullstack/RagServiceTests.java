package com.example.aifullstack;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aifullstack.service.RagService;
import org.junit.jupiter.api.Test;

class RagServiceTests {

    private final RagService ragService = new RagService();

    @Test
    void retrievesControllerKnowledgeForRelatedQuestion() {
        var chunks = ragService.retrieve("Controller 如何接收 HTTP 请求？");

        assertThat(chunks).isNotEmpty();
        assertThat(chunks.getFirst().title()).contains("Controller");
    }

    @Test
    void returnsHelpfulMessageWhenKnowledgeIsMissing() {
        var answer = ragService.fallbackAnswer("天气怎么样？", ragService.retrieve("天气怎么样？"));

        assertThat(answer).contains("暂时没有找到");
    }
}
