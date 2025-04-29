package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.dto.api.ErrorResponse;
import cl.tenpo.learning.reactive.tasks.task2.exception.BadRequestException;
import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;

@Component
public class ExceptionHandler {
    private final Map<
            Class<? extends Throwable>,
            BiFunction<? extends Throwable, ServerRequest, Mono<ServerResponse>>> handlers =
            Map.ofEntries(
                    entry(BadRequestException.class, this::handleBadRequestException),
                    entry(NotFoundException.class, this::handleNotFoundException),
                    entry(UnauthorizedException.class, this::handleUnauthorizedException),
                    entry(InternalException.class, this::handleInternalException)
            );

    private <T extends Throwable> Map.Entry<Class<T>, BiFunction<T, ServerRequest, Mono<ServerResponse>>> entry(
            final Class<T> clazz, final BiFunction<T, ServerRequest, Mono<ServerResponse>> handler
    ) {
        return Map.entry(clazz, handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> Mono<ServerResponse> handle(final T err, final ServerRequest request) {
        return ((BiFunction<T, ServerRequest, Mono<ServerResponse>>)
                handlers.getOrDefault(err.getClass(), this::handleUnknownException))
                .apply(err, request);
    }

    private Mono<ServerResponse> handleBadRequestException(final Throwable throwable, final ServerRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "invalid_request", throwable.getMessage());
    }

    private Mono<ServerResponse> handleNotFoundException(final Throwable throwable, final ServerRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "not_found", throwable.getMessage());
    }

    private Mono<ServerResponse> handleUnauthorizedException(final Throwable throwable, final ServerRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "unauthorized_request", throwable.getMessage());
    }

    private Mono<ServerResponse> handleInternalException(final Throwable throwable, final ServerRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", throwable.getMessage());
    }

    private Mono<ServerResponse> handleUnknownException(final Throwable throwable, final ServerRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "unknown_error", throwable.getMessage());
    }

    private Mono<ServerResponse> buildErrorResponse(final HttpStatus status, final String type, final String message) {
        return ServerResponse.status(status).bodyValue(ErrorResponse.builder().type(type).message(message).build());
    }

}
