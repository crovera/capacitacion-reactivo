package cl.tenpo.learning.reactive.tasks.task2.dto.api;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String type,
        String message
) {
}
