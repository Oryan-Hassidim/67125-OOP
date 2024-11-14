package pepse.world.daynight;

import danogl.GameObject;

/**
 * A factory for creating sun halos in the game world.
 */
public interface SunHaloFactory {
    /**
     * The tag of the sun halo.
     */
    public static final String SUN_HALO_TAG = "sun halo";

    /**
     * Creates a new sun halo.
     * 
     * @param sun the sun
     * @return the sun halo
     */
    public GameObject provide(GameObject sun);
}
