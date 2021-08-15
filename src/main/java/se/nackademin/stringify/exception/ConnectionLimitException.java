package se.nackademin.stringify.exception;

/**
 * Used when a client is trying to connect to a ChatSession that already has reached maximum amount of connections.
 */
public class ConnectionLimitException extends RuntimeException {

    public ConnectionLimitException(String message) {
        super(message);
    }
}
