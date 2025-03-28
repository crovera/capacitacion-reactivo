package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CalculatorService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question5 {

    private final CalculatorService calculatorService;
    private final UserService userService;

    /**
     * Utilizando el método calculate del CalculatorService, aplicarlo a un rango de números a partir del 100 hasta el 1000.
     * El cálculo puede devolver un resultado, un vacío o un error. Si y solo sí no hay errores, proceder a retornar un
     * nombre al azar a través del UserService (findFirstName), de lo contrario debe retornar “Chuck Norris”.
     * El cálculo debe aplicarse a los números en orden ascendiente.
     */
    public Mono<String> question5A() {
        return makeCalculations(100, 1000)
                .then(Mono.defer(userService::findFirstName))
                .onErrorReturn("Chuck Norris");
    }

    /**
     * Utilizando el método calculate del CalculatorService, aplicarlo a un rango de números a partir del 100 hasta el 1000.
     * El cálculo puede devolver un resultado, un vacío o un error. Si y solo sí no hay errores, proceder a retornar los
     * primeros 3 nombres sin importar repetidos a través del UserService (findAllNames), de lo contrario debe
     * completarse el publisher. El cálculo debe aplicarse a los números sin orden alguno.
     */
    public Flux<String> question5B() {
        return makeCalculations(100, 1000)
                .thenMany(Flux.defer(userService::findAllNames))
                .take(3)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .onErrorResume(err -> Flux.empty());
    }

    private Flux<BigDecimal> makeCalculations(int start, int end) {
        return Flux.range(start, end - start + 1)
                .map(BigDecimal::valueOf)
                .flatMap(calculatorService::calculate)
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()))
                .doOnComplete(() -> log.info("Emitted onComplete"));
    }
}
