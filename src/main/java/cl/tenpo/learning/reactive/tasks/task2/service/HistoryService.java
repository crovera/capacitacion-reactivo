package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.model.History;
import cl.tenpo.learning.reactive.tasks.task2.repository.HistoryMongoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class HistoryService {
    private final String className = getClass().getSimpleName();
    private final HistoryMongoRepository historyMongoRepository;

    public Flux<History> getAllEntries() {
        return historyMongoRepository.findAll();
    }

    public Mono<History> saveEntry(final History history) {
        return historyMongoRepository.save(history)
                .doOnNext(usr -> log.info("[{}] History saved: [{}]", className, usr))
                .onErrorMap(err -> new InternalError("Error saving history"));
    }
}
