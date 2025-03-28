package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question2 {

    private final CountryService countryService;

    /**
     * Utilizando el método findAllCountries del CountryService,
     * realizar una lógica que devuelva los primeros 5 países distintos.
     */
    public Flux<String> question2A() {
        return countryService.findAllCountries()
                .distinct()
                .take(5)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnDiscard(String.class, discard -> log.info("Emitted onDiscard: {}", discard))
                .doOnComplete(() -> log.info("Emitted onComplete"))
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()));
    }

    /**
     * Utilizando el método findAllCountries del CountryService,
     * realizar una lógica que emita países hasta que encuentre a “Argentina”.
     */
    public Flux<String> question2B() {
        return countryService.findAllCountries()
                .takeUntil(country -> country.equals("Argentina"))
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnDiscard(String.class, discard -> log.info("Emitted onDiscard: {}", discard))
                .doOnComplete(() -> log.info("Emitted onComplete"))
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()));
    }

    /**
     * Utilizando el método findAllCountries del CountryService,
     * realizar una lógica que emita países siempre y cuando no encuentre a “France”.
     */
    public Flux<String> question2C() {
        return countryService.findAllCountries()
                .takeWhile(k -> !k.equals("France"))
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnDiscard(String.class, discard -> log.info("Emitted onDiscard: {}", discard))
                .doOnComplete(() -> log.info("Emitted onComplete"))
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()));
    }
}
