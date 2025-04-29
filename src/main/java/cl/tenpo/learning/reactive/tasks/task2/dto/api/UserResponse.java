package cl.tenpo.learning.reactive.tasks.task2.dto.api;

import lombok.Builder;

@Builder
public record UserResponse(Integer userId, String username) {
}
