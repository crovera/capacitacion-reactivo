package cl.tenpo.learning.reactive.tasks.task2.dto.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(name = "users")
public record UserEntity(
        @Id
        Integer id,
        String name
) {
}

