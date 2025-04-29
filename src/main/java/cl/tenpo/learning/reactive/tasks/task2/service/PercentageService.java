package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.messaging.PercentageErrorProducer;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import cl.tenpo.learning.reactive.tasks.task2.repository.PercentageCacheRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
@AllArgsConstructor
public class PercentageService {
    private static final Integer MAX_ATTEMPTS = 3;

    private final String className = getClass().getSimpleName();
    private final PercentageClient percentageClient;
    private final PercentageCacheRepository percentageCacheRepository;
    private final PercentageErrorProducer percentageErrorProducer;

    public Mono<Percentage> getPercentage() {
        return getPercentageFromApi()
                .flatMap(percentageCacheRepository::save)
                .onErrorResume(err -> percentageCacheRepository.get())
                .doOnNext(percentage -> log.info("[{}] Percentage: {}", className, percentage))
                .doOnError(err -> log.error("[{}] Error getting percentage", className, err))
                .onErrorMap(err -> new InternalException("Error getting percentage", err));
    }

    private Mono<Percentage> getPercentageFromApi() {
        return Mono.defer(percentageClient::getPercentage)
                .retryWhen(Retry.max(MAX_ATTEMPTS)
                        .doBeforeRetry(rs -> log.info("[{}] Retrying get percentage", className))
                        .onRetryExhaustedThrow((spec, rs) -> rs.failure()))
                .onErrorResume(err -> percentageErrorProducer.sendErrorEvent(err)
                        .then(Mono.error(err)));
    }
}
