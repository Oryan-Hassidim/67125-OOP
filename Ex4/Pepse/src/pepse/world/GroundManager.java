package pepse.world;

import java.util.List;

/**
 * An interface for a manager that manages the ground in the game world.
 */
public interface GroundManager {

    /**
     * Returns the height of the ground at the given x coordinate.
     * 
     * @param x the x coordinate
     * @return the height of the ground at the given x coordinate
     */
    float groundHeightAt(float x);

    /**
     * Creates a list of blocks for a given range of x coordinates.
     * 
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     * @return a list of blocks for the given range of x coordinates
     */
    List<Block> createInRange(int minX, int maxX);

}