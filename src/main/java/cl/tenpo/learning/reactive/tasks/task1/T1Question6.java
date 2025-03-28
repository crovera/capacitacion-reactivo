package cl.tenpo.learning.reactive.tasks.task1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question6 {

    /**
     * Desarrollar un publisher que emita el precio de una acción en vivo cada 500ms.
     * Consideraciones: El precio solo puede moverse entre 1 y 500.
     * Configurar el publisher para que los suscriptores cuando se conecten vean la data en tiempo real,
     * perdiendo todos los precios ya emitidos por el publisher antes que estos se conecten.
     */
    public ConnectableFlux<Double> question6() {
        Random random = new Random();
        return Flux.<Double>generate(sink -> sink.next(random.nextDouble(1, 500)))
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .delayElements(Duration.ofMillis(500))
                .publish();
    }
}