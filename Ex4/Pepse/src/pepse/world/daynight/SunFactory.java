package pepse.world.daynight;

import pepse.world.GameObjectFactory;

/**
 * A factory for creating suns in the game world.
 * 
 * @author Oryan Hassidim
 */
public interface SunFactory extends GameObjectFactory {
    /**
     * The tag of the sun.
     */
    public static final String SUN_TAG = "SUN";
}
