package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.dto.api.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.api.CalculationResponse;
import cl.tenpo.learning.reactive.tasks.task2.exception.BadRequestException;
import cl.tenpo.learning.reactive.tasks.task2.service.CalculationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@AllArgsConstructor
public class CalculationHandler {
    private final CalculationService calculationService;

    public Mono<ServerResponse> calculate(final ServerRequest request) {
        return extractBody(request)
                .flatMap(this::validate)
                .flatMap(body -> calculationService.calculate(body.number1(), body.number2()))
                .map(result -> CalculationResponse.builder().result(result).build())
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    private Mono<CalculationRequest> extractBody(final ServerRequest request) {
        return request.bodyToMono(CalculationRequest.class)
                .onErrorMap(err -> new BadRequestException("Invalid request body"));
    }

    private Mono<CalculationRequest> validate(final CalculationRequest request) {
        return Mono.just(request)
                .filter(req -> Objects.nonNull(req.number1()))
                .filter(req -> Objects.nonNull(req.number2()))
                .switchIfEmpty(Mono.error(new BadRequestException("Numbers can not be null")));
    }
}
