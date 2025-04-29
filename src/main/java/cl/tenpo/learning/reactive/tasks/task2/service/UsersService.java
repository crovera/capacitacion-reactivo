package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.exception.BadRequestException;
import cl.tenpo.learning.reactive.tasks.task2.model.User;
import cl.tenpo.learning.reactive.tasks.task2.repository.UsersR2DBCRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Slf4j
@Service
@AllArgsConstructor
public class UsersService {
    private final String className = getClass().getSimpleName();
    private final UsersR2DBCRepository usersR2DBCRepository;

    public Mono<User> getUser(final String username) {
        return usersR2DBCRepository.findByUsername(username)
                .onErrorMap(err -> new InternalError("Error getting user: " + username));
    }

    public Mono<User> createUser(final User user) {
        return usersR2DBCRepository.existsByUsername(user.name())
                .filter(Predicate.not(Boolean::booleanValue))
                .flatMap(__ -> saveUser(user))
                .switchIfEmpty(Mono.error(new BadRequestException("User already exist")));
    }

    private Mono<User> saveUser(final User user) {
        return usersR2DBCRepository.save(user)
                .doOnNext(usr -> log.info("[{}] User saved: [{}]", className, usr))
                .onErrorMap(err -> new InternalError("Error saving user"));
    }

    public Mono<User> deleteUser(final String username) {
        return usersR2DBCRepository.findByUsername(username)
                .flatMap(this::deleteUser)
                .switchIfEmpty(Mono.error(new BadRequestException("User doesn't exist")));
    }

    private Mono<User> deleteUser(final User user) {
        return usersR2DBCRepository.delete(user)
                .doOnNext(usr -> log.info("[{}] User deleted: [{}]", className, user))
                .onErrorMap(err -> new InternalError("Error deleting user"));
    }

}
