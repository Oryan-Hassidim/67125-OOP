package pepse.world.daynight;

import pepse.world.GameObjectFactory;

/**
 * A factory for creating nights in the game world.
 * 
 * @author Oryan Hassidim
 */
public interface NightFactory extends GameObjectFactory {
    /**
     * The tag of the night.
     */
    public static String NIGHT_TAG = "night";
}
