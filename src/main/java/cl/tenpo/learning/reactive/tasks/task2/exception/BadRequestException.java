package cl.tenpo.learning.reactive.tasks.task2.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(final String message) {
        super(message);
    }
}
