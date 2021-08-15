package se.nackademin.stringify.exception;

/**
 * Used when a Profile cannot be found in the database
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
