package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.messaging.PercentageErrorProducer;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import cl.tenpo.learning.reactive.tasks.task2.repository.PercentageCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PercentageServiceTest {
    @Mock
    private PercentageClient percentageClient;
    @Mock
    private PercentageCacheRepository percentageCacheRepository;
    @Mock
    private PercentageErrorProducer percentageErrorProducer;

    @InjectMocks
    private PercentageService target;

    @Test
    void getPercentageOk() {
        // Assemble
        final Percentage percentage = new Percentage(BigDecimal.valueOf(0.1));

        when(percentageClient.getPercentage()).thenReturn(Mono.just(percentage));
        when(percentageCacheRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Percentage> actual = target.getPercentage();

        // Assert
        StepVerifier.create(actual)
                .assertNext(act -> assertThat(act).isEqualTo(percentage))
                .verifyComplete();

        verify(percentageClient).getPercentage();
        verify(percentageCacheRepository).save(percentage);
        verify(percentageCacheRepository, never()).get();
        verify(percentageErrorProducer, never()).sendErrorEvent(any());
    }

    @Test
    void getPercentageFromApiAfterRetry() {
        // Assemble
        final Percentage percentage = new Percentage(BigDecimal.valueOf(0.1));
        final Throwable exception = new InternalException("Error getting percentage from api");

        when(percentageClient.getPercentage())
                .thenReturn(Mono.error(exception))
                .thenReturn(Mono.error(exception))
                .thenReturn(Mono.just(percentage));
        when(percentageCacheRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Percentage> actual = target.getPercentage();

        // Assert
        StepVerifier.create(actual)
                .assertNext(act -> assertThat(act).isEqualTo(percentage))
                .verifyComplete();

        verify(percentageClient, times(3)).getPercentage();
        verify(percentageCacheRepository).save(percentage);
        verify(percentageCacheRepository, never()).get();
        verify(percentageErrorProducer, never()).sendErrorEvent(any());
    }

    @Test
    void getPercentageFromCache() {
        // Assemble
        final Percentage percentage = new Percentage(BigDecimal.valueOf(0.1));
        final Throwable exception = new InternalException("Error getting percentage from api");

        when(percentageClient.getPercentage()).thenReturn(Mono.error(exception));
        when(percentageCacheRepository.get()).thenReturn(Mono.just(percentage));

        // Act
        Mono<Percentage> actual = target.getPercentage();

        // Assert
        StepVerifier.create(actual)
                .assertNext(act -> assertThat(act).isEqualTo(percentage))
                .verifyComplete();

        verify(percentageClient, times(4)).getPercentage();
        verify(percentageCacheRepository, never()).save(percentage);
        verify(percentageCacheRepository).get();
        verify(percentageErrorProducer).sendErrorEvent(exception);
    }

    @Test
    void getPercentageFail() {
        // Assemble
        final Percentage percentage = new Percentage(BigDecimal.valueOf(0.1));
        final Throwable apiException = new InternalException("Error getting percentage from api");
        final Throwable cacheException = new InternalException("Error getting percentage from cache");

        when(percentageClient.getPercentage()).thenReturn(Mono.error(apiException));
        when(percentageCacheRepository.get()).thenReturn(Mono.error(cacheException));

        // Act
        Mono<Percentage> actual = target.getPercentage();

        // Assert
        StepVerifier.create(actual)
                .expectErrorSatisfies(err -> assertThat(err)
                        .isInstanceOf(InternalException.class)
                        .hasMessage("Error getting percentage"))
                .verify();

        verify(percentageClient, times(4)).getPercentage();
        verify(percentageCacheRepository, never()).save(percentage);
        verify(percentageCacheRepository).get();
        verify(percentageErrorProducer).sendErrorEvent(apiException);
    }

}