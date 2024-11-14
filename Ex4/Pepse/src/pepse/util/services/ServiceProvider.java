package pepse.util.services;

/**
 * Functional interface for providing a service.
 * 
 * @param <T> The type of the service.
 */
public interface ServiceProvider<T> {
    /**
     * Provides the service.
     * 
     * @return The service.
     */
    T provide();
}
