package cl.tenpo.learning.reactive.tasks.task2.messaging;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@AllArgsConstructor
public class PercentageErrorConsumer {
    private final String className = getClass().getSimpleName();
    private final ReactiveKafkaConsumerTemplate<String, PercentageErrorEvent> percentageErrorConsumerTemplate;

    @PostConstruct
    public void run() {
        percentageErrorStream().subscribe();
    }

    private Flux<PercentageErrorEvent> percentageErrorStream() {
        return percentageErrorConsumerTemplate
                .receiveAutoAck()
                .map(ConsumerRecord::value)
                .doOnNext(evt -> log.info("[{}] Event received -> {}", className, evt))
                .onErrorContinue((err, evt) -> log.error("[{}] Error receiving event -> {}", className, evt, err));
    }
}
