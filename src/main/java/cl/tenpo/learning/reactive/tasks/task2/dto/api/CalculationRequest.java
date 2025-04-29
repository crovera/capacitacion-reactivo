package cl.tenpo.learning.reactive.tasks.task2.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalculationRequest(
        @JsonProperty("number_1")
        BigDecimal number1,
        @JsonProperty("number_2")
        BigDecimal number2
) {
}
