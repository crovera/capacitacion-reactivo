package cl.tenpo.learning.reactive.tasks.task2.config;

import cl.tenpo.learning.reactive.tasks.task2.handler.CalculationHandler;
import cl.tenpo.learning.reactive.tasks.task2.handler.ExceptionHandler;
import cl.tenpo.learning.reactive.tasks.task2.handler.HistoryHandler;
import cl.tenpo.learning.reactive.tasks.task2.handler.UsersHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@AllArgsConstructor
public class RouterConfig {
    private final CalculationHandler calculatorHandler;
    private final UsersHandler usersHandler;
    private final HistoryHandler historyHandler;
    private final ExceptionHandler exceptionHandler;

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/ping", req -> ServerResponse.ok().bodyValue("pong"))
                .POST("/calculation", calculatorHandler::calculate)
                .POST("/users", usersHandler::createUser)
                .DELETE("/users/{username}", usersHandler::deleteUser)
                .GET("/history", historyHandler::getHistory)
                .onError(Throwable.class, exceptionHandler::handle)
                .build();
    }
}
