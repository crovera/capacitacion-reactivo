package cl.tenpo.learning.reactive.tasks.task2.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
public class ResponseDecorator extends ServerHttpResponseDecorator {
    private final String className = getClass().getSimpleName();
    private String responseBody;

    public ResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    @NonNull
    public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        Mono<DataBuffer> buffer = Mono.from(body);
        return super.writeWith(buffer.doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.capacity());
                dataBuffer.toByteBuffer(byteBuffer);
                Channels.newChannel(byteArrayOutputStream).write(byteBuffer);
                responseBody = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
            } catch (Exception ex) {
                log.error("[{}] Response error", className, ex);
            }
        }));
    }
}
