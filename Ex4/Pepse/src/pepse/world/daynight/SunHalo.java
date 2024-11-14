package pepse.world.daynight;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * A class representing the sun halo in the game world.
 */
public class SunHalo {
    /**
     * The radius of the sun halo.
     */
    private static final float SUN_HALO_RADIUS = 70f;

    /**
     * Creates a new sun halo.
     * 
     * @param sun the sun
     * @return the sun halo
     */
    public static GameObject create(GameObject sun) {
        var halo = new GameObject(Vector2.ZERO, Vector2.ONES.mult(SUN_HALO_RADIUS * 2),
                new OvalRenderable(new Color(255, 255, 0, 20)));
        halo.setTag(SunHaloFactory.SUN_HALO_TAG);
        halo.setCoordinateSpace(sun.getCoordinateSpace());
        halo.addComponent(f -> halo.setCenter(sun.getCenter()));
        return halo;
    }

}
