package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class CalculationService {
    private final String className = getClass().getSimpleName();
    private final PercentageService percentageService;

    public Mono<BigDecimal> calculate(final BigDecimal number1, final BigDecimal number2) {
        return Mono.zip(
                        Mono.just(number1.add(number2)),
                        percentageService.getPercentage(),
                        (sum, per) -> sum.multiply(per.percentage()).add(sum).stripTrailingZeros()
                )
                .doOnError(err -> log.error("[{}] Calculation error", className, err))
                .onErrorMap(err -> new InternalException("Calculation error", err));
    }
}
