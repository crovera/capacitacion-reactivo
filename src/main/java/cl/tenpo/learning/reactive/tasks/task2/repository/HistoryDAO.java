package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.dto.entity.HistoryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HistoryDAO extends ReactiveMongoRepository<HistoryEntity, String> {
}
