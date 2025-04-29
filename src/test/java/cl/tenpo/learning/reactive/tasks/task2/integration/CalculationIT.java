package cl.tenpo.learning.reactive.tasks.task2.integration;

import cl.tenpo.learning.reactive.tasks.task2.T2Application;
import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import cl.tenpo.learning.reactive.tasks.task2.repository.PercentageCacheRepository;
import cl.tenpo.learning.reactive.tasks.task2.testConfig.E2ETestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest(classes = T2Application.class)
@ActiveProfiles("test")
@Import(E2ETestConfiguration.class)
public class CalculationIT {
    @MockBean
    private PercentageClient percentageClient;
    @SpyBean
    private PercentageCacheRepository percentageCacheRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void calculationShouldBeOk() {
        when(percentageClient.getPercentage()).thenReturn(Mono.just(new Percentage(BigDecimal.valueOf(0.1))));

        webTestClient
                .post()
                .uri("/calculation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"number_1\":5,\"number_2\":5}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.result").isEqualTo("11");
    }

    @Test
    void calculationShouldBeOkFallback() {
        when(percentageClient.getPercentage()).thenReturn(Mono.error(new InternalException("error")));
        when(percentageCacheRepository.get()).thenReturn(Mono.just(new Percentage(BigDecimal.valueOf(0.1))));

        webTestClient
                .post()
                .uri("/calculation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"number_1\":5,\"number_2\":5}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.result").isEqualTo("11");
    }

    @Test
    void calculationShouldFail() {
        when(percentageClient.getPercentage()).thenReturn(Mono.error(new InternalException("error")));
        when(percentageCacheRepository.get()).thenReturn(Mono.error(new NotFoundException("not found")));

        webTestClient
                .post()
                .uri("/calculation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"number_1\":5,\"number_2\":5}")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.type").isEqualTo("internal_error")
                .jsonPath("$.message").isEqualTo("Calculation error");
    }

    @Test
    void calculationInvalidNumber() {
        webTestClient
                .post()
                .uri("/calculation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"number_1\":\"f\",\"number_2\":5}")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("invalid_request")
                .jsonPath("$.message").isEqualTo("Invalid request body");
    }

    @Test
    void calculationMissingNumber() {
        webTestClient
                .post()
                .uri("/calculation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("invalid_request")
                .jsonPath("$.message").isEqualTo("Numbers can not be null");
    }
}
