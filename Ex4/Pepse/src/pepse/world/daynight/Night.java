package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.GameState;
import pepse.util.Services;

import java.awt.Color;

/**
 * A class representing the night in the game world.
 * 
 * @author Oryan Hassidim
 */
public class Night {
    /**
     * The opacity of the night at midday.
     */
    private static final float MIDDAY_OPACITY = 0f;
    /**
     * The opacity of the night at midnight.
     */
    private static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Creates a new night.
     * 
     * @return the night
     */
    public static GameObject create() {
        var dims = Services.getService(WindowController.class).getWindowDimensions();
        GameObject night = new GameObject(Vector2.ZERO, dims, new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NightFactory.NIGHT_TAG);
        var gameState = Services.getService(GameState.class);
        gameState.onHourInDayChanged().add(
                (gameState1, args) -> {
                    var hour = args.getArgs().getNewValue();
                    var opaqueness = Transition.CUBIC_INTERPOLATOR_FLOAT.interpolate(
                            MIDDAY_OPACITY, MIDNIGHT_OPACITY, (24 - Math.abs(hour - 12)) / 12);
                    night.renderer().setOpaqueness(opaqueness);
                });
        return night;
    }
}
