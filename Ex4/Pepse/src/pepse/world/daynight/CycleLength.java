package pepse.world.daynight;

import pepse.util.services.ServiceProvider;

/**
 * An interface for a service that provides the length of the day-night cycle.
 * 
 * @author Oryan Hassidim
 */
public interface CycleLength extends ServiceProvider<Float> {
    /**
     * The default length of the day-night cycle.
     */
    public static final float DEFAULT_CYCLE_LENGTH = 30f;

    /**
     * Returns the length of the day-night cycle.
     * 
     * @return the length of the day-night cycle
     */
    @Override
    default Float provide() {
        return DEFAULT_CYCLE_LENGTH;
    }
}
