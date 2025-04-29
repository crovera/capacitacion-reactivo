package cl.tenpo.learning.reactive.tasks.task2.dto.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

@Builder
@Document(collection = "history")
public record HistoryEntity(
        @Id
        String id,
        String verb,
        String url,
        MultiValueMap<String, String> params,
        String request,
        String response,
        Integer status,
        String username,
        LocalDateTime date
) {
}
