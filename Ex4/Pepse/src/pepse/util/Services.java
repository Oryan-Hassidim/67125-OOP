package pepse.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import pepse.util.services.NoPublicConstructorFoundException;
import pepse.util.services.ServiceNotFoundException;
import pepse.util.services.ServiceProvider;

/**
 * <p>
 * This class is used to register and get services for dependency injection.
 * It is a simple implementation of the Service Locator pattern.
 * It is used to avoid the use of static methods and to make the code much more
 * testable.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Services.registerService(MyService.class, new MyService());
 * MyService service = Services.getService(MyService.class);
 * </pre>
 * 
 * @author Oryan Hassidim
 */
public final class Services {
    /** The services collection. */
    private static Map<Class<?>, ServiceProvider<? extends Object>> services = new HashMap<>();

    /**
     * Constructs a new Services.
     */
    private Services() {
        super();
    }

    /**
     * Registers a service.
     * 
     * @param <T>         the type of the service
     * @param serviceType the type of the service
     * @param provider    the service provider
     * @throws IllegalArgumentException if the service type or provider is null
     */
    public static <T> void addService(Class<T> serviceType, ServiceProvider<T> provider)
            throws IllegalArgumentException {
        Validator.validateNotNull(serviceType, "serviceType");
        Validator.validateNotNull(provider, "service");
        services.put(serviceType, provider);
    }

    /**
     * Registers a singleton service.
     * 
     * @param <T>         the type of the service
     * @param serviceType the type of the service
     * @param service     the service instance
     */
    public static <T> void addSingleton(Class<T> serviceType, T service) throws IllegalArgumentException {
        Validator.validateNotNull(service, "service");
        var provider = new ServiceProvider<T>() {
            @Override
            public T provide() {
                return service;
            }
        };
        Services.addService(serviceType, provider);
    }

    /**
     * Registers a service implementation.
     * 
     * @param <T>                the type of the service
     * @param <U>                the type of the implementation
     * @param serviceType        the type of the service
     * @param implementationType the type of the implementation
     * @throws IllegalArgumentException          if the service type or
     *                                           implementation type
     *                                           is null
     * @throws NoPublicConstructorFoundException if no public constructor is found
     *                                           for the implementation type
     */
    public static <T, U extends T> void addService(Class<T> serviceType, Class<U> implementationType)
            throws IllegalArgumentException, NoPublicConstructorFoundException {
        Validator.validateNotNull(serviceType, "serviceType");
        Validator.validateNotNull(implementationType, "implementationType");
        Constructor<?> constructor = TryGetPublicConstructor(implementationType);
        var params = constructor.getParameters();

        ServiceProvider<T> provider = () -> {
            try {
                var args = Arrays.stream(params).map(p -> Services.getService(p.getType())).toArray();
                return implementationType.cast(constructor.newInstance(args));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Services.addService(serviceType, provider);
    }


    /**
     * Tries to get a public constructor for the implementation type.
     * @param <U> the type of the implementation
     * @param type the type of the implementation
     * @return the public constructor
     * @throws NoPublicConstructorFoundException if no public constructor is found
     */
    private static <U> Constructor<?> TryGetPublicConstructor(Class<U> type)
            throws NoPublicConstructorFoundException {
        Constructor<?> constructor;
        try {
            constructor = Arrays.stream(type.getDeclaredConstructors())
                    .filter(c -> Modifier.isPublic(c.getModifiers()))
                    .min((c1, c2) -> Integer.compare(c1.getParameterCount(), c2.getParameterCount()))
                    .orElseThrow(() -> new RuntimeException(new NoPublicConstructorFoundException(
                            "No public constructor found for " + type.getName())));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof NoPublicConstructorFoundException)
                throw (NoPublicConstructorFoundException) e.getCause();
            else
                throw e;
        }
        return constructor;
    }

    /**
     * Registers a singleton service implementation.
     * 
     * @param <T>                the type of the service
     * @param <U>                the type of the implementation
     * @param serviceType        the type of the service
     * @param implementationType the type of the implementation
     * @throws IllegalArgumentException          if the service type or
     *                                           implementation type
     *                                           is null
     * @throws NoPublicConstructorFoundException if no public constructor is found
     *                                           for the implementation type
     */
    public static <T, U extends T> void addSingleton(Class<T> serviceType, Class<U> implementationType)
            throws IllegalArgumentException, NoPublicConstructorFoundException {
        Validator.validateNotNull(serviceType, "serviceType");
        Validator.validateNotNull(implementationType, "implementationType");
        Constructor<?> constructor = TryGetPublicConstructor(implementationType);
        var params = constructor.getParameters();

        ServiceProvider<T> provider = new ServiceProvider<T>() {
            private T instance;

            @Override
            public T provide() {
                if (instance == null) {
                    try {
                        var args = Arrays.stream(params).map(p -> Services.getService(p.getType())).toArray();
                        instance = implementationType.cast(constructor.newInstance(args));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return instance;
            }
        };
        Services.addService(serviceType, provider);
    }

    /**
     * Gets a service.
     * 
     * @param <T>         the type of the service
     * @param serviceType the type of the service
     * @return the service
     * @throws IllegalArgumentException if the service type is null
     * @throws ServiceNotFoundException if the service is not found
     */
    public static <T> T getService(Class<T> serviceType)
            throws IllegalArgumentException, ServiceNotFoundException {
        Validator.validateNotNull(serviceType, "serviceType");
        var provider = services.get(serviceType);
        if (provider == null)
            throw new ServiceNotFoundException("Service not found: " + serviceType.getName());

        return serviceType.cast(provider.provide());
    }
}
