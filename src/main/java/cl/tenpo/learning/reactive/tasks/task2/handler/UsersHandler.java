package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.dto.api.UserRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.api.UserResponse;
import cl.tenpo.learning.reactive.tasks.task2.exception.BadRequestException;
import cl.tenpo.learning.reactive.tasks.task2.model.User;
import cl.tenpo.learning.reactive.tasks.task2.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@AllArgsConstructor
public class UsersHandler {
    private final UsersService usersService;

    public Mono<ServerResponse> createUser(final ServerRequest request) {
        return extractBody(request)
                .flatMap(this::validate)
                .flatMap(this::toModel)
                .flatMap(usersService::createUser)
                .map(result -> new UserResponse(result.id(), result.name()))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> deleteUser(final ServerRequest request) {
        return extractUsername(request)
                .flatMap(usersService::deleteUser)
                .map(result -> new UserResponse(result.id(), result.name()))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    private Mono<UserRequest> extractBody(final ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
                .onErrorMap(err -> new BadRequestException("Invalid request body"));
    }

    private Mono<String> extractUsername(final ServerRequest request) {
        return Mono.fromCallable(() -> request.pathVariable("username"))
                .onErrorMap(err -> new BadRequestException(err.getMessage()));
    }

    private Mono<UserRequest> validate(final UserRequest user) {
        return Mono.just(user)
                .filter(req -> Objects.nonNull(req.username()))
                .switchIfEmpty(Mono.error(new BadRequestException("Username can not be null")));
    }

    private Mono<User> toModel(final UserRequest request) {
        return Mono.just(request).map(usrReq -> User.builder().name(usrReq.username()).build());
    }
}
