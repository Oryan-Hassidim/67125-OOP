package pepse.util;

/**
 * Class for representing a value change.
 * The value change is used to represent a change in a value as a event
 * argument.
 * 
 * @param <T> The type of the value.
 * @author Oryan Hassidim
 */
public class ValueChanged<T> {
    /**
     * The old value.
     */
    private T oldValue;
    /**
     * The new value.
     */
    private T newValue;

    /**
     * Initializes a new instance of the ValueChanged class.
     * 
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    public ValueChanged(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Gets the old value.
     * 
     * @return The old value.
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * Gets the new value.
     * 
     * @return The new value.
     */
    public T getNewValue() {
        return newValue;
    }
}
