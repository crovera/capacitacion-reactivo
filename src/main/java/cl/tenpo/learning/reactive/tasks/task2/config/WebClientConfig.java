package cl.tenpo.learning.reactive.tasks.task2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
public class WebClientConfig {
    private final ObjectMapper mapper;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 3000;
    private static final int WRITE_TIMEOUT = 5000;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(buildClientConnector())
                .exchangeStrategies(buildStrategy())
                .build();
    }

    private ClientHttpConnector buildClientConnector() {
        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT, TimeUnit.MILLISECONDS))
                );
        return new ReactorClientHttpConnector(httpClient.wiretap(true));
    }

    private ExchangeStrategies buildStrategy() {
        return ExchangeStrategies.builder().codecs(clientDefaultCodecsConfigurer -> {
            clientDefaultCodecsConfigurer.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
            clientDefaultCodecsConfigurer.defaultCodecs()
                    .jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
        }).build();
    }
}
