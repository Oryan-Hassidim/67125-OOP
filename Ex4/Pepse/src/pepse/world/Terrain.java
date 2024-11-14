package pepse.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.util.Seed;
import pepse.util.Services;

/**
 * A class representing the terrain in the game world.
 * 
 * @author Oryan Hassidim
 */
public class Terrain implements GroundManager {
    /**
     * The factor to use for the y coordinate when filling blocks.
     */
    private static final float FILL_BLOCKS_Y_FACTOR = 1.5f;
    /**
     * The size of a block.
     */
    private static final int BLOCK_SIZE = Block.SIZE;
    /**
     * The factor to use for the noise generator.
     */
    private static final int NOISE_FACTOR = BLOCK_SIZE * 5;
    /**
     * The base color of the ground.
     */
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    /**
     * The height of the ground at x = 0.
     */
    private int groundHeightAtX0;
    /**
     * The dimensions of the window.
     */
    private Vector2 windowDimensions;
    /**
     * The seed to use for the noise generator.
     */
    private int seed;
    /**
     * The noise generator to use for the terrain.
     */
    private NoiseGenerator noise;

    /**
     * Creates a new terrain.
     */
    public Terrain() {
        this.windowDimensions = Services.getService(WindowController.class).getWindowDimensions();
        this.seed = Services.getService(Seed.class).getSeed();
        groundHeightAtX0 = (int) (windowDimensions.y() * 0.7);
        noise = new NoiseGenerator(seed, groundHeightAtX0);
    }

    /**
     * Returns the height of the ground at the given x coordinate.
     * 
     * @param x the x coordinate
     * @return the height of the ground at the given x coordinate
     */
    @Override
    public float groundHeightAt(float x) {
        x = (int) Math.floor((float) (x) / BLOCK_SIZE) * BLOCK_SIZE;
        var y = (float) (groundHeightAtX0 + noise.noise(x, NOISE_FACTOR));
        return (int) Math.ceil(y / BLOCK_SIZE) * BLOCK_SIZE;
    }

    /**
     * Creates a list of blocks for a given range of x coordinates.
     * 
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     * @return a list of blocks for the given range of x coordinates
     */
    @Override
    public List<Block> createInRange(int minX, int maxX) {
        minX = (int) Math.floor((float) (minX) / BLOCK_SIZE) * BLOCK_SIZE;
        maxX = (int) Math.ceil((float) (maxX) / BLOCK_SIZE) * BLOCK_SIZE;
        var blocks = new ArrayList<Block>();
        var Y = (int) windowDimensions.y() * FILL_BLOCKS_Y_FACTOR;
        for (int x = minX; x < maxX; x += BLOCK_SIZE) {
            var height = (int) groundHeightAt(x);
            for (int y = height; y < Y; y += BLOCK_SIZE) {
                blocks.add(new Block(new Vector2(x, y),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))));
            }
        }
        return blocks;
    }
}