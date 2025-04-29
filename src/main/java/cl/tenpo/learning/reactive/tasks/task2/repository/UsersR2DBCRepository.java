package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.dto.entity.UserEntity;
import cl.tenpo.learning.reactive.tasks.task2.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class UsersR2DBCRepository {
    private final UsersDAO usersDAO;

    public Mono<Boolean> existsByUsername(final String username) {
        return usersDAO.existsByName(username);
    }

    public Mono<User> findByUsername(final String username) {
        return usersDAO.findByName(username)
                .map(this::toModel);
    }

    public Mono<User> save(final User user) {
        return Mono.just(user)
                .map(this::toEntity)
                .flatMap(usersDAO::save)
                .map(this::toModel);
    }

    public Mono<User> delete(final User user) {
        return Mono.just(user)
                .map(this::toEntity)
                .flatMap(usersDAO::delete)
                .thenReturn(user);
    }

    private User toModel(final UserEntity entity) {
        return new User(entity.id(), entity.name());
    }

    private UserEntity toEntity(final User user) {
        return new UserEntity(user.id(), user.name());
    }
}
