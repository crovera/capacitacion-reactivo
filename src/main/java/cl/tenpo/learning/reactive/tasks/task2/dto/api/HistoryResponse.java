package cl.tenpo.learning.reactive.tasks.task2.dto.api;

import lombok.Builder;
import org.springframework.util.MultiValueMap;

@Builder
public record HistoryResponse(
        String verb,
        String url,
        MultiValueMap<String, String> params,
        String request,
        String response,
        Integer status,
        String username,
        String date
) {
}
