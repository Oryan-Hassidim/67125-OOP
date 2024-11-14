package pepse.util;

/**
 * Class for validating parameters.
 * The validator is used to validate parameters.
 * 
 * @author Oryan Hassidim
 */
public final class Validator {

    /**
     * Prevents the creation of an instance of the class.
     */
    private Validator() {
    }

    /**
     * The message format for null parameters.
     */
    private static final String NULL_MESSAGE_FORMAT = "the parameter %s cannot be null.";

    /**
     * Validates that the object is not null.
     * 
     * @param object The object to validate.
     * @param name   The name of the object.
     * @throws IllegalArgumentException If the object is null.
     */
    public static void validateNotNull(Object object, String name) throws IllegalArgumentException {
        if (object == null)
            throw new IllegalArgumentException(String.format(NULL_MESSAGE_FORMAT, name));
    }
}
