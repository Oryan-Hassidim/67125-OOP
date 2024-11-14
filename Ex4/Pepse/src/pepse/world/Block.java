package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A class representing a block of ground in the game world.
 * 
 * @author Oryan Hassidim
 */
public class Block extends GameObject {
    /**
     * The size of a block.
     */
    public static final int SIZE = 30;

    /**
     * Creates a new block at the given position.
     * 
     * @param topLeftCorner the top left corner of the block
     * @param renderable    the renderable to use for the block
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Returns if the block is equal to another object, which is true if the
     * other object is a block and has the same top left corner.
     * 
     * @param obj the object to compare to
     * @return treu if the block is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Block && getTopLeftCorner().equals(((Block) obj).getTopLeftCorner());
    }

    /**
     * Returns the hash code of the block, which is the hash code of the top left
     * corner.
     * 
     * @return the hash code of the block
     */
    @Override
    public int hashCode() {
        return getTopLeftCorner().hashCode();
    }
}