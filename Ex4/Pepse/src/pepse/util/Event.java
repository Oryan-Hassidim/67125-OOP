package pepse.util;

import java.util.HashSet;

/**
 * Class for representing and handling events.
 * The event is a collection of event handlers, and an event token for adding
 * and removing event handlers.
 * 
 * @param <S> The sender type.
 * @param <A> The argument type.
 * @author Oryan Hassidim
 */
public class Event<S, A> {
    /**
     * The event handlers collection.
     */
    private HashSet<EvnentHandler<S, A>> listeners = new HashSet<>();

    /**
     * The event token for adding and removing event handlers.
     */
    private EventToken<S, A> token = new EventToken<S, A>() {
        @Override
        public void add(EvnentHandler<S, A> handler) {
            listeners.add(handler);
        }

        @Override
        public void remove(EvnentHandler<S, A> handler) {
            listeners.remove(handler);
        }
    };

    /**
     * Gets the event token.
     * 
     * @return The event token.
     */
    public EventToken<S, A> getToken() {
        return token;
    }

    /**
     * Invokes the event.
     * 
     * @param sender The sender.
     * @param args   The event arguments.
     */
    public void invoke(S sender, A args) {
        for (var listener : listeners) {
            listener.handle(sender, new EventArgs<A>(args));
        }
    }
}
