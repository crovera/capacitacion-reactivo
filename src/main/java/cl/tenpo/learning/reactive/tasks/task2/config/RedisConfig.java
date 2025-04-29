package cl.tenpo.learning.reactive.tasks.task2.config;

import cl.tenpo.learning.reactive.tasks.task2.model.Percentage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Percentage> percentageRedisTemplate(
            final ReactiveRedisConnectionFactory connectionFactory
    ) {
        final RedisSerializationContext<String, Percentage> context = RedisSerializationContext
                .<String, Percentage>newSerializationContext()
                .key(RedisSerializer.string())
                .value(new Jackson2JsonRedisSerializer<>(Percentage.class))
                .hashKey(RedisSerializer.string())
                .hashValue(new Jackson2JsonRedisSerializer<>(Percentage.class))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

}
