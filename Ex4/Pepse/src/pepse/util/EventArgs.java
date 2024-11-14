package pepse.util;

/**
 * Class for representing event arguments.
 * 
 * @param <A> The argument type.
 */
public class EventArgs<A> {
    /**
     * The event arguments.
     */
    private A args;

    /**
     * Create a new event arguments.
     * 
     * @param args The event arguments.
     */
    public EventArgs(A args) {
        this.args = args;
    }

    /**
     * Gets the event arguments.
     * 
     * @return The event arguments.
     */
    public A getArgs() {
        return args;
    }
}