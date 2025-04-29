package cl.tenpo.learning.reactive.tasks.task2.filter;


import cl.tenpo.learning.reactive.tasks.task2.model.History;
import cl.tenpo.learning.reactive.tasks.task2.service.HistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cl.tenpo.learning.reactive.tasks.task2.util.Headers.X_USERNAME;

@Slf4j
@Component
@AllArgsConstructor
public class HistoryFilter implements WebFilter {
    private static final String HISTORY_PATH = "/learning-reactive/history";
    private final HistoryService historyService;

    @Override
    @NonNull
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        RequestDecorator requestDecorator = new RequestDecorator(exchange.getRequest());
        ResponseDecorator responseDecorator = new ResponseDecorator(exchange.getResponse());

        return chain.filter(exchange.mutate().request(requestDecorator).response(responseDecorator).build())
                .doFinally(s -> saveInteraction(
                        exchange, requestDecorator.getRequestBody(), responseDecorator.getResponseBody()));
    }

    private void saveInteraction(final ServerWebExchange exchange, final String request, final String response) {
        Mono.just(exchange)
                .filter(exc -> !HISTORY_PATH.equals(exc.getRequest().getPath().toString()))
                .flatMap(exc -> historyService.saveEntry(buildHistory(exchange, request, response)))
                .subscribe();
    }

    private History buildHistory(final ServerWebExchange exchange, final String request, final String response) {
        return History.builder()
                .verb(exchange.getRequest().getMethod().name())
                .url(exchange.getRequest().getPath().toString())
                .params(getParams(exchange))
                .request(request)
                .response(response)
                .status(getStatus(exchange))
                .date(LocalDateTime.now())
                .username(getUsername(exchange))
                .build();
    }

    private static MultiValueMap<String, String> getParams(ServerWebExchange exchange) {
        return !exchange.getRequest().getQueryParams().isEmpty() ? exchange.getRequest().getQueryParams() : null;
    }

    private static Integer getStatus(ServerWebExchange exchange) {
        return Objects.nonNull(exchange.getResponse().getStatusCode())
                ? exchange.getResponse().getStatusCode().value() : null;
    }

    private String getUsername(final ServerWebExchange exchange) {
        List<String> usernameHeaders = exchange.getRequest().getHeaders().get(X_USERNAME);
        return Objects.nonNull(usernameHeaders) && !usernameHeaders.isEmpty() ? usernameHeaders.getFirst() : null;
    }
}
