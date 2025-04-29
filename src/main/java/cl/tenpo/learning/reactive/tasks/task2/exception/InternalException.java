package cl.tenpo.learning.reactive.tasks.task2.exception;

public class InternalException extends RuntimeException {
    public InternalException(final String message) {
        super(message);
    }

    public InternalException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
