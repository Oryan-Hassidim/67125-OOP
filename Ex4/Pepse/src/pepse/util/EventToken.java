package pepse.util;

/**
 * Event token Interface, for adding and removing event handlers.
 * 
 * @param <S> The sender type.
 * @param <A> The argument type.
 * @author Oryan Hassidim
 */
public interface EventToken<S, A> {
    /**
     * Add an event handler.
     * 
     * @param handler The event handler.
     */
    public void add(EvnentHandler<S, A> handler);

    /**
     * Remove an event handler.
     * 
     * @param handler The event handler.
     */
    public void remove(EvnentHandler<S, A> handler);
}
