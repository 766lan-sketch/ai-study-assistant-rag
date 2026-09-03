package com.example.aifullstack.repository;

import com.example.aifullstack.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop20ByOrderByCreatedAtAsc();
}
//可以拆开理解：
//        - find：查询
//- Top20：最多查询20条
//- OrderByCreatedAt：按照创建时间排序
//- Asc：从早到晚