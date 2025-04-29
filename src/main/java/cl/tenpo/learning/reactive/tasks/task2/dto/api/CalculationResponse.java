package cl.tenpo.learning.reactive.tasks.task2.dto.api;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalculationResponse(BigDecimal result) {
}
