package pepse.util;

/**
 * Functional interface for handling events.
 * 
 * @param <S> The sender type.
 * @param <A> The argument type.
 */
@FunctionalInterface
public interface EvnentHandler<S, A> {
    /**
     * Handle the event.
     * 
     * @param sender The sender.
     * @param args   The event arguments.
     */
    void handle(S sender, EventArgs<A> args);
}