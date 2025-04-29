package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.dto.entity.HistoryEntity;
import cl.tenpo.learning.reactive.tasks.task2.model.History;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class HistoryMongoRepository {
    private final HistoryDAO historyDAO;

    public Flux<History> findAll() {
        return historyDAO.findAll()
                .map(this::toModel);
    }

    public Mono<History> save(final History history) {
        return Mono.just(history)
                .map(this::toEntity)
                .flatMap(historyDAO::save)
                .map(this::toModel);
    }

    private History toModel(final HistoryEntity entity) {
        return History.builder()
                .id(entity.id())
                .verb(entity.verb())
                .url(entity.url())
                .params(entity.params())
                .request(entity.request())
                .response(entity.response())
                .status(entity.status())
                .username(entity.username())
                .date(entity.date())
                .build();
    }

    private HistoryEntity toEntity(final History history) {
        return HistoryEntity.builder()
                .id(history.id())
                .verb(history.verb())
                .url(history.url())
                .params(history.params())
                .request(history.request())
                .response(history.response())
                .status(history.status())
                .username(history.username())
                .date(history.date())
                .build();
    }
}
