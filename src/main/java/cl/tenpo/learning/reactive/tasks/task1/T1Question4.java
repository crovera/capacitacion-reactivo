package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question4 {

    private final CountryService countryService;

    /**
     * Asumiendo que el método findAllCountries es costoso de ejecutar, consumir dicho método del CountryService con el
     * fin de calcular la cantidad de repeticiones por país en un lote de 200 países emitidos, de manera asincrónica
     * (el resultado debe verse por consola en forma de mapa). Para el mismo lote de 200 países, se debe retornar el
     * listado de países en orden alfabético sin repetidos.
     */
    public Flux<String> question4A() {
        Flux<String> countries = countryService.findAllCountries()
                .take(200)
                .subscribeOn(Schedulers.boundedElastic())
                .cache();

        countries
                .groupBy(String::toString)
                .flatMap(group -> Mono.just(group.key()).zipWith(group.count()))
                .collectMap(Tuple2::getT1, Tuple2::getT2)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .subscribe();

        return countries
                .distinct()
                .sort()
                .doOnNext(next -> log.info("Emitted onNext: {}", next));
    }

}
