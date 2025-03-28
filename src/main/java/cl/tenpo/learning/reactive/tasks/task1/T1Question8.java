package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.AuthorizationTimeoutException;
import cl.tenpo.learning.reactive.utils.exception.PaymentProcessingException;
import cl.tenpo.learning.reactive.utils.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class T1Question8 {

    private final TransactionService transactionService;

    /**
     * Subscribirse al servicio TransactionService.authorizeTransaction(), teniendo en cuenta las siguientes condiciones.
     * Consideraciones:
     * 1. Si el servicio tarda más de 3 segundos en enviarnos la respuesta, capturar el timeout y
     * convertirlo en AuthorizationTimeoutException.
     * 2. Establecer una estrategia de 3 reintentos cada 500 ms para cualquier error del servicio excepto los Timeouts.
     * 3. Capturar todas las excepciones que devuelva el servicio y transformarlas en una PaymentProcessingException
     * (excluyendo los timeouts, que ya los mapeamos en AuthorizationTimeoutException).
     */
    public Mono<String> question8() {
        return transactionService.authorizeTransaction(11111)
                .doOnNext(next -> log.info("Emitted onNext: {}", next))
                .doOnSuccess(success -> log.info("Emitted onSuccess: {}", success))
                .timeout(Duration.of(3, ChronoUnit.SECONDS))
                .onErrorMap(TimeoutException.class, err -> new AuthorizationTimeoutException(err.getMessage()))
                .onErrorMap(Predicate.not(AuthorizationTimeoutException.class::isInstance),
                        err -> new PaymentProcessingException(err.getMessage(), err))
                .doOnError(err -> log.error("Emitted onError: {}", err.getMessage()))
                .retryWhen(handleRetry());
    }

    private static RetryBackoffSpec handleRetry() {
        return Retry.fixedDelay(3, Duration.of(500, ChronoUnit.MILLIS))
                .filter(err -> !(err instanceof AuthorizationTimeoutException))
                .doBeforeRetry(retrySignal -> log.error("Emitted beforeRetry: retries {} error {}",
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                .doAfterRetry(retrySignal -> log.error("Emitted afterRetry: retries {} error {}",
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()));
    }
}
