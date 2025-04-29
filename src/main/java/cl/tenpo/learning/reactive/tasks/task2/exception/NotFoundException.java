package cl.tenpo.learning.reactive.tasks.task2.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
