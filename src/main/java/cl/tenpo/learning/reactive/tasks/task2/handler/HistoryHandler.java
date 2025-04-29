package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.dto.api.HistoryResponse;
import cl.tenpo.learning.reactive.tasks.task2.exception.BadRequestException;
import cl.tenpo.learning.reactive.tasks.task2.exception.UnauthorizedException;
import cl.tenpo.learning.reactive.tasks.task2.model.History;
import cl.tenpo.learning.reactive.tasks.task2.model.User;
import cl.tenpo.learning.reactive.tasks.task2.service.HistoryService;
import cl.tenpo.learning.reactive.tasks.task2.service.UsersService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;

import static cl.tenpo.learning.reactive.tasks.task2.util.Headers.X_USERNAME;

@Slf4j
@Component
@AllArgsConstructor
public class HistoryHandler {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final HistoryService historyService;
    private final UsersService usersService;

    public Mono<ServerResponse> getHistory(final ServerRequest request) {
        return extractUsername(request)
                .flatMap(this::validate)
                .flatMapMany(usr -> historyService.getAllEntries())
                .map(this::mapResponse)
                .collectList()
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    private Mono<String> extractUsername(final ServerRequest request) {
        return Mono.fromCallable(() -> request.headers().header(X_USERNAME).getFirst())
                .onErrorMap(err -> new BadRequestException("X-Username header is required"));
    }

    private Mono<User> validate(final String username) {
        return usersService.getUser(username).switchIfEmpty(
                Mono.error(new UnauthorizedException(String.format("User %s is not authorized", username))));
    }

    private HistoryResponse mapResponse(final History history) {
        return HistoryResponse.builder()
                .verb(history.verb())
                .url(history.url())
                .params(history.params())
                .request(history.request())
                .response(history.response())
                .status(history.status())
                .username(history.username())
                .date(DATE_TIME_FORMAT.format(history.date()))
                .build();
    }

}
