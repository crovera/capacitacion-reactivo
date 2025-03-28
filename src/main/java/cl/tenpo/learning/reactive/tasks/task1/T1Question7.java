package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.model.UserAccount;
import cl.tenpo.learning.reactive.utils.service.AccountService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question7 {

    private final UserService userService;
    private final AccountService accountService;

    /**
     * Desarrollar una lógica para generar un UserAccount obteniendo la información de los siguientes servicios
     * UserService.getUserById() y AccountService.getAccountByUserId().
     */
    public Mono<UserAccount> question7(String userId) {
        return Mono.zip(userService.getUserById(userId), accountService.getAccountByUserId(userId), UserAccount::new)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnSuccess(success -> log.info("Emitted onSuccess: {}", success));
    }
}
