package cl.tenpo.learning.reactive.tasks.task2.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
public class RequestDecorator extends ServerHttpRequestDecorator {
    private final String className = getClass().getSimpleName();
    private String requestBody;

    public RequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    @NonNull
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.capacity());
                dataBuffer.toByteBuffer(byteBuffer);
                Channels.newChannel(byteArrayOutputStream).write(byteBuffer);
                requestBody = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
            } catch (IOException ex) {
                log.error("[{}] Request error", className, ex);
            }
        });
    }

}
