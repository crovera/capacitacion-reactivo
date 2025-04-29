package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@AllArgsConstructor
public class PercentageCacheRepository {
    private static final String KEY = "PERCENTAGE";
    private static final Long DURATION = 30L;

    private final String className = getClass().getSimpleName();
    private final ReactiveRedisTemplate<String, Percentage> percentageRedisTemplate;

    public Mono<Percentage> get() {
        return percentageRedisTemplate.opsForValue()
                .get(KEY)
                .doOnNext(percentage -> log.info("[{}] Percentage found in cache: {}", className, percentage))
                .doOnError(err -> log.error("[{}] Error getting percentage from cache", className, err))
                .onErrorMap(err -> new InternalException("Error getting percentage from cache", err))
                .switchIfEmpty(Mono.error(new NotFoundException("Percentage not found")));
    }

    public Mono<Percentage> save(final Percentage value) {
        return percentageRedisTemplate.opsForValue()
                .set(KEY, value, Duration.ofMinutes(DURATION))
                .doOnNext(percentage -> log.info("[{}] Percentage saved in cache: {}", className, percentage))
                .thenReturn(value)
                .doOnError(err -> log.error("[{}] Error saving percentage to cache", className, err))
                .onErrorResume(err -> Mono.just(value));
    }
}
