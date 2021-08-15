package se.nackademin.stringify.exception;

/**
 * Used when key dont match the {@code Key.class} pattern.
 */
public class InvalidKeyException extends RuntimeException {

    public InvalidKeyException() {
        super("Invalid key: Incorrect argument cannot be handled");
    }
}
