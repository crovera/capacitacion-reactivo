package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.model.Page;
import cl.tenpo.learning.reactive.utils.service.CountryService;
import cl.tenpo.learning.reactive.utils.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question3 {

    private final CountryService countryService;
    private final TranslatorService translatorService;

    /**
     * Considerando que se recibe un objeto de tipo Page<String>, realizar una lógica para emitir todos los elementos
     * internos de la página. Tener en cuenta que el objeto page puede ser nulo.
     */
    public Flux<String> question3A(Page<String> page) {
        return Mono.justOrEmpty(page)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnSuccess(success -> log.info("Emitted onSuccess: {}", success))
                .flatMapIterable(Page::items)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnComplete(() -> log.info("Emitted onComplete"));
    }

    /**
     * Utilizando el método findCurrenciesByCountry del CountryService, realizar una lógica que emita todas las
     * distintas monedas asociadas a los países de la página que llega por parámetro.
     */
    public Flux<String> question3B(String country) {
        return countryService.findCurrenciesByCountry(country)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnComplete(() -> log.info("Emitted onComplete"));
    }

    /**
     * Utilizando el método findAllCountries del CountryService y el translate del TranslatorService,
     * traducir los nombres de los 3 primeros países. Tener en cuenta que el TranslatorService puede retornar
     * null si no sabe traducir el país que recibe por parámetro.
     */
    public Flux<String> question3C() {
        return countryService.findAllCountries()
                .mapNotNull(translatorService::translate)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .take(3)
                .doOnComplete(() -> log.info("Emitted onComplete"));
    }

}
