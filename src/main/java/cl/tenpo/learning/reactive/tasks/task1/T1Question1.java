package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.utils.exception.UserServiceException;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question1 {

    private final UserService userService;

    /**
     * Utilizando el método findFirstName del UserService, realizar una lógica que procese un único nombre para mapearlo
     * a su longitud de caractéres en caso de que el nombre comience con la letra A.
     * En su defecto, el suscriptor debe recibir el valor -1.
     */
    public Mono<Integer> question1A() {
        return userService.findFirstName()
                .filter(name -> name.startsWith("A"))
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnDiscard(String.class, discard -> log.info("Emitted onDiscard: {}", discard))
                .map(String::length)
                .defaultIfEmpty(-1)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()));
    }

    /**
     * Utilizando los métodos findFirstName, existsByName, update e insert del UserService,
     * realizar una lógica que actualice el registro en la BD si ya existe. Si no existe, debe insertarlo.
     */
    public Mono<String> question1B() {
        return userService.findFirstName()
                .flatMap(this::updateOrCreate)
                .doOnError(err -> log.error("Error getting name: {}", err.getMessage(), err));
    }

    private Mono<String> updateOrCreate(String name) {
        return Mono.just(name)
                .filterWhen(userService::existByName)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnDiscard(String.class, discard -> log.info("Emitted onDiscard: {}", discard))
                .flatMap(userService::update)
                .switchIfEmpty(Mono.defer(() -> userService.insert(name)))
                .doOnNext(next -> log.info("Name {} {}", name, next));
    }

    /**
     * Utilizando el método findFirstByName del UserService, devolver el valor retornado por el servicio.
     * Si el servicio no devuelve nada, retornar un ResourceNotFoundException.
     * En caso de que el servicio arroje un error, arrojar una UserServiceException.
     */
    public Mono<String> question1C(String name) {
        return userService.findFirstByName(name)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .switchIfEmpty(Mono.error(ResourceNotFoundException::new))
                .onErrorMap(Predicate.not(ResourceNotFoundException.class::isInstance), err -> new UserServiceException())
                .doOnError(err -> log.error("Error {} getting name {}", err.getClass().getSimpleName(), name, err));
    }

}
