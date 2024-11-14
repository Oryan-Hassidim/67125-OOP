package pepse.ui;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.GameState;
import pepse.util.Services;

/**
 * The energy view factory for viewing the avatar's energy.
 * 
 * @author Oryan Hassidim
 */
public final class EnergyView {
    /**
     * The energy view constructor.
     */
    private EnergyView() {
    }

    /**
     * Create a new energy view.
     * 
     * @return The energy view.
     */
    public static GameObject create() {
        var energyView = new GameObject(Vector2.ZERO, Vector2.ONES.mult(40f), null);
        var textRenderable = new TextRenderable("");
        energyView.renderer().setRenderable(textRenderable);
        var gameState = Services.getService(GameState.class);
        gameState.onAvatarEnergyChanged().add(
                (avatar, args) -> textRenderable.setString(args.getArgs().getNewValue() + "%"));
        energyView.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return energyView;
    }
}
