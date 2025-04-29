package cl.tenpo.learning.reactive.tasks.task2.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(final String message) {
        super(message);
    }
}
