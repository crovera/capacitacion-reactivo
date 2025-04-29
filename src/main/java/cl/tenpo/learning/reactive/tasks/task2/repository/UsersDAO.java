package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.dto.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UsersDAO extends R2dbcRepository<UserEntity, Integer> {
    Mono<Boolean> existsByName(String name);
    Mono<UserEntity> findByName(String name);
}
