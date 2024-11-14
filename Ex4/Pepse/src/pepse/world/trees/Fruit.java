package pepse.world.trees;

import java.awt.Color;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.Food;
import pepse.util.Services;
import pepse.world.daynight.CycleLength;

/**
 * A class representing a fruit in the game world.
 * 
 * @see Food
 * @author Oryan Hassidim
 */
class Fruit extends GameObject implements Food {
    /**
     * The location to move the fruit to when it is eaten.
     */
    private static final float OUT_OF_BOUNDS_LOCATION = -2000;
    /**
     * The energy provided by the fruit when eaten.
     */
    private static final float FRUIT_ENERGY = 10f;

    /**
     * The color of the fruit.
     */
    protected Color color;
    /**
     * The tree that the fruit is on.
     */
    private GameObject tree;

    /**
     * Creates a new fruit.
     * 
     * @param position the position of the fruit
     * @param tree     the tree that the fruit is on
     */
    public Fruit(Vector2 position, GameObject tree) {
        super(position, Vector2.ONES.mult(Flora.LEAF_SIZE), null);
        this.tree = tree;
        color = ColorSupplier.approximateColor(Color.RED);
        this.renderer().setRenderable(new OvalRenderable(color));
    }

    /**
     * Eats the fruit.
     * 
     * @return The amount of energy the food provides.
     */
    @Override
    public float eaten() {
        var center = this.getCenter();
        this.setCenter(Vector2.of(this.getCenter().x() ,OUT_OF_BOUNDS_LOCATION));
        new ScheduledTask(tree, Services.getService(CycleLength.class).provide(), false,
                () -> this.setCenter(center));
        return FRUIT_ENERGY;
    }
}