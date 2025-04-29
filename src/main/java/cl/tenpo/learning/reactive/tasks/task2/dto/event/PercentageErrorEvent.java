package cl.tenpo.learning.reactive.tasks.task2.dto.event;

import lombok.Builder;

@Builder
public record PercentageErrorEvent(String error) {
}
