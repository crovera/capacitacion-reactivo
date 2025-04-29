package cl.tenpo.learning.reactive.tasks.task2.messaging;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;

@Slf4j
@Component
@AllArgsConstructor
public class PercentageErrorProducer {
    private final String className = getClass().getSimpleName();

    private final ReactiveKafkaProducerTemplate<String, PercentageErrorEvent> percentageErrorProducerTemplate;

    public Mono<Void> sendErrorEvent(final Throwable throwable) {
        return Mono.just(throwable)
                .map(err -> PercentageErrorEvent.builder().error(err.getMessage()).build())
                .flatMap(evt -> percentageErrorProducerTemplate.send(CR_RETRY_EXHAUSTED, evt))
                .doOnSuccess(sr -> log.info("[{}] Success sending event to [{}]", className, CR_RETRY_EXHAUSTED))
                .doOnError(err -> log.error("[{}] Could not send event to [{}]", className, CR_RETRY_EXHAUSTED, err))
                .onErrorResume(err -> Mono.empty())
                .then();
    }
}
