package cl.tenpo.learning.reactive.tasks.task2.model;

import lombok.Builder;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

@Builder
public record History(
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
