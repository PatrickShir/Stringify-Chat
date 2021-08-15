package se.nackademin.stringify.exception;

/**
 * Used when a ChatSession cant be found
 */
public class ChatSessionNotFoundException extends RuntimeException {

    public ChatSessionNotFoundException(String message) {
        super(message);
    }
}
