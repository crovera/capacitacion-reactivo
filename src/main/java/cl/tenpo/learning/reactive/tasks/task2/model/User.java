package cl.tenpo.learning.reactive.tasks.task2.model;

import lombok.Builder;

@Builder
public record User(
        Integer id,
        String name
) {
}
