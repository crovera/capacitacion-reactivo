package cl.tenpo.learning.reactive.tasks.task2.integration;

import cl.tenpo.learning.reactive.tasks.task2.T2Application;
import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.exception.InternalException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.model.History;
import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import cl.tenpo.learning.reactive.tasks.task2.repository.HistoryMongoRepository;
import cl.tenpo.learning.reactive.tasks.task2.repository.PercentageCacheRepository;
import cl.tenpo.learning.reactive.tasks.task2.testConfig.E2ETestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cl.tenpo.learning.reactive.tasks.task2.util.Headers.X_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest(classes = T2Application.class)
@ActiveProfiles("test")
@Import(E2ETestConfiguration.class)
public class HistoryIT {
    @MockBean
    private PercentageClient percentageClient;
    @MockBean
    private HistoryMongoRepository historyMongoRepository;

    @Autowired
    private WebTestClient webTestClient;

    //@Test
    void historyShouldBeOk() {
        final History history = History.builder()
                .url("/users")
                .verb("GET")
                .username("mudo")
                .request("{\"username\":\"mudo\"}")
                .response("{\"user_id\":1,\"username\":\"mudo\"}")
                .status(200)
                .date(LocalDateTime.parse("2025-04-28T16:43"))
                .build();

        when(historyMongoRepository.findAll()).thenReturn(Flux.just(history));
        when(historyMongoRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        webTestClient
                .post()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"mudo\"}")
                .exchange();

        webTestClient
                .get()
                .uri("/history")
                .accept(MediaType.APPLICATION_JSON)
                .header(X_USERNAME, "mudo")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].url").isEqualTo("/users")
                .jsonPath("$[0].verb").isEqualTo("GET")
                .jsonPath("$[0].username").isEqualTo("mudo")
                .jsonPath("$[0].request").isEqualTo("{\"username\":\"mudo\"}")
                .jsonPath("$[0].response").isEqualTo("{\"user_id\":1,\"username\":\"mudo\"}")
                .jsonPath("$[0].status").isEqualTo("200");
    }

    //@Test
    void historyUnauthorized() {
        webTestClient
                .get()
                .uri("/history")
                .accept(MediaType.APPLICATION_JSON)
                .header(X_USERNAME, "goku")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.type").isEqualTo("unauthorized_request")
                .jsonPath("$.message").isEqualTo("User goku is not authorized");
    }

    //@Test
    void historyMissingUser() {
        webTestClient
                .get()
                .uri("/history")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("invalid_request")
                .jsonPath("$.message").isEqualTo("X-Username header is required");
    }
}
