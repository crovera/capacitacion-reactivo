package cl.tenpo.learning.reactive.tasks.task2.client;

import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class PercentageClient {
    private static final String URL = "http://localhost:8083/learning-reactive/external-api/percentage";

    private final String className = getClass().getSimpleName();
    private final WebClient webClient;

    public Mono<Percentage> getPercentage() {
        return webClient.get()
                .uri(URL)
                .retrieve()
                .bodyToMono(Percentage.class)
                .doOnError(err -> log.error("[{}] Error getting percentage from api", className, err))
                .onErrorMap(err -> new InternalException("Error getting percentage from api", err));
    }
}
