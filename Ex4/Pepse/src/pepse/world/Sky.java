package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.Services;

import java.awt.Color;

/**
 * A class representing the sky in the game world.
 * 
 * @author Oryan Hassidim
 */
public class Sky {
    /**
     * the basic sky color.
     */
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * creates a sky game object.
     * 
     * @return the sky game object.
     */
    public static GameObject create() {
        var windowSize = Services.getService(WindowController.class).getWindowDimensions();
        GameObject sky = new GameObject(Vector2.ZERO, windowSize, new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SkyFactory.SKY_TAG);
        return sky;
    }
}
