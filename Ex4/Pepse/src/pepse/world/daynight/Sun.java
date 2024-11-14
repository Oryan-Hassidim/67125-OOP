package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.WindowController;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.GameState;
import pepse.util.Services;
import pepse.world.GroundManager;

import java.awt.Color;

/**
 * A class representing the sun in the game world.
 * 
 * @author Oryan Hassidim
 */
public class Sun {
    /**
     * A constant representing half.
     */
    private static final float HALF = 0.5f;
    /**
     * The radius of the sun.
     */
    private static final float SUN_RADIUS = 40f;

    /**
     * Creates a new sun.
     * 
     * @return the sun
     */
    public static GameObject create() {
        var dims = Services.getService(WindowController.class).getWindowDimensions();
        var groundHeight = Services.getService(GroundManager.class).groundHeightAt(dims.mult(HALF).x());
        var sun = new GameObject(Vector2.ZERO, Vector2.ONES.mult(SUN_RADIUS * 2f),
                new OvalRenderable(Color.YELLOW));
        var cycleCenter = Vector2.of(dims.x() * HALF, groundHeight);
        var initialSunCenter = Vector2.of(dims.x() * HALF, 2 * groundHeight - dims.y() * .25f);
        sun.setCenter(initialSunCenter);
        sun.setTag(SunFactory.SUN_TAG);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        var gameState = Services.getService(GameState.class);
        gameState.onHourInDayChanged().add(
                (gameState1, args) -> {
                    var hour = args.getArgs().getNewValue();
                    var angle = Transition.LINEAR_INTERPOLATOR_FLOAT.interpolate(0f, 360f, hour / 24);
                    sun.setCenter(initialSunCenter.subtract(cycleCenter)
                            .rotated(angle)
                            .add(cycleCenter));
                });
        return sun;
    }
}
