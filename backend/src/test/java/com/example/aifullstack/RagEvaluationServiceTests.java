package com.example.aifullstack;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aifullstack.service.RagEvaluationService;
import com.example.aifullstack.service.RagService;
import org.junit.jupiter.api.Test;

class RagEvaluationServiceTests {
    @Test
    void builtInKnowledgeBaseReachesExpectedTop1Accuracy() {
        var result = new RagEvaluationService(new RagService()).evaluate();

        assertThat(result.totalCases()).isEqualTo(7);
        assertThat(result.top1Accuracy()).isGreaterThanOrEqualTo(85.0);
        assertThat(result.cases()).allMatch(item -> !item.retrievedTitle().isBlank());
    }
}
