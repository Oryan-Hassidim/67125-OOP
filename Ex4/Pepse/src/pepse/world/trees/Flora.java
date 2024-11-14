package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.components.Transition;
import danogl.components.Transition.TransitionType;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.GameState;
import pepse.util.ColorSupplier;
import pepse.util.Services;
import pepse.world.GroundManager;
import pepse.world.Avatar.AvatarState;

import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;
import java.util.stream.Stream;
import java.util.Objects;

/**
 * A class for creating trees, leaves and fruits in the game world.
 * 
 * @author Oryan Hassidim
 */
public final class Flora {
    /**
     * The constant zero.
     */
    private static final int ZERO = 0;
    /**
     * The constant half.
     */
    private static final float HALF = 0.5f;
    /**
     * The constant for the seed addition for the trees.
     */
    private static final int TREES_SEED_ADD = 2;
    /**
     * the minimum value for the random tree height, before cutting.
     */
    private static final int MIN_RANDOM_TREE_HEIGHT = 30;
    /**
     * the maximum value for the random tree height, before cutting.
     */
    private static final int MAX_RANDOM_TREE_HEIGHT = 90;
    /**
     * the minimum value for the tree height.
     */
    private static final int MIN_TREE_HEIGHT = 70;
    /**
     * the minimal space between trees.
     */
    private static final int TREE_SPACE = 30;
    /**
     * the probability for a tree to be created.
     */
    private static final int TREES_PROB = 5;
    /**
     * the width of a tree.
     */
    private static final float TREE_WIDTH = 30f;
    /**
     * the base color of the tree.
     */
    private static final Color TREE_BASE_COLOR = new Color(100, 50, 20);
    /**
     * the constant to add to the seed for the leaves.
     */
    private static final int LEAVES_SEED_ADD = 3;
    /**
     * the size of a leaf.
     */
    static final float LEAF_SIZE = 10f;
    /**
     * the standard deviation for the x position of the leaves.
     */
    private static final float LEAF_X_STDDEV = 1.8f;
    /**
     * the mean for the y position of the leaves.
     */
    private static final float LEAF_Y_MEAN = -2;
    /**
     * the standard deviation for the y position of the leaves.
     */
    private static final float LEAF_Y_STDDEV = 2.3f;
    /**
     * the minimum number of leaves per tree.
     */
    private static final int MIN_LEAVES = 60;
    /**
     * the maximum number of leaves per tree.
     */
    private static final int MAX_LEAVES = 100;
    /**
     * the base color of the leaves.
     */
    private static final Color LEAF_BASE_COLOR = new Color(50, 200, 30);
    /**
     * The constant for the rotation of the leaves.
     */
    private static final float LEAF_ROTATION = 20f;
    /**
     * The constant for the width of the leaves on transition.
     */
    private static final float LEAF_WIDTH_ON_TRANSITION = 0.7f;
    /**
     * The constant for the minimum time for the regular transition.
     */
    private static final float MIN_REGULAR_TRANSITION_TIME = 8f;
    /**
     * The constant for the maximum time for the regular transition.
     */
    private static final float MAX_REGULAR_TRANSITION_TIME = 14f;
    /**
     * The constant for the minimum time for the jumping animation.
     */
    private static final float MIN_JUMPING_ANIMATION_TIME = 2f;
    /**
     * The constant for the maximum time for the jumping animation.
     */
    private static final float MAX_JUMPING_ANIMATION_TIME = 4f;
    /**
     * The constant for a full rotation - 360 degrees.
     */
    private static final float FULL_ROTATION = 360f;
    /**
     * The constant for the base color of the fruits on jump.
     */
    private static final Color LEAF_BASE_COLOR_ON_JUMP = Color.YELLOW;

    /**
     * A private constructor to prevent instantiation.
     */
    private Flora() {
    }

    /**
     * Creates a list of trees for a given range of x coordinates.
     * 
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     * @return a list of trees for the given range of x coordinates
     */
    public static List<GameObject> createInRange(float minX, float maxX) {
        ArrayList<GameObject> trees = new ArrayList<>();
        ArrayList<Runnable> onJump = new ArrayList<>(); // a list of actions to perform when the avatar jumps
        var groundManager = Services.getService(GroundManager.class);
        var seed = Services.getService(pepse.util.Seed.class).getSeed() + TREES_SEED_ADD;
        for (float x = minX; x < maxX; x += TREE_SPACE) {
            var rand = new Random(Objects.hash(seed, x));
            var treeHeight = rand.nextInt(MIN_RANDOM_TREE_HEIGHT, MAX_RANDOM_TREE_HEIGHT);
            if (treeHeight < MIN_TREE_HEIGHT || treeHeight % TREES_PROB != ZERO) {
                continue;
            }
            var groundHeight = groundManager.groundHeightAt(x);
            var color = ColorSupplier.approximateColor(TREE_BASE_COLOR);
            var tree = new GameObject(
                    Vector2.of(x - HALF * TREE_WIDTH, groundHeight - treeHeight),
                    new Vector2(TREE_WIDTH, treeHeight),
                    new RectangleRenderable(color));
            tree.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            tree.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            trees.add(tree);

            onJump.add(() -> animateTreeColorChange(rand, color, tree));
        }
        var gameState = Services.getService(GameState.class);
        gameState.onAvatarStateChanged().add((avatar, args) -> {
            if (args.getArgs().getNewValue() != AvatarState.JUMP)
                return;
            for (var r : onJump) {
                r.run();
            }
        });
        return trees;
    }

    /**
     * Animates the color change of a tree.
     * 
     * @param rand  the random number generator
     * @param color the current color of the tree
     * @param tree  the tree
     * @return the transition
     */
    private static Transition<Color> animateTreeColorChange(Random rand, Color color, GameObject tree) {
        return new Transition<>(tree, c -> tree.renderer().setRenderable(new RectangleRenderable(c)),
                color,
                ColorSupplier.approximateColor(ColorSupplier.approximateColor(TREE_BASE_COLOR.brighter())),
                Flora::interpolateColorLineary,
                rand.nextFloat(MIN_JUMPING_ANIMATION_TIME, MAX_JUMPING_ANIMATION_TIME),
                TransitionType.TRANSITION_ONCE,
                () -> tree.renderer().setRenderable(new RectangleRenderable(color)));
    }

    /**
     * Creates a leaf.
     * 
     * @param rand the random number generator
     * @return the leaf
     */
    private static GameObject createLeaf(Random rand) {
        var leaf = new GameObject(Vector2.ZERO, Vector2.ONES.mult(LEAF_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_BASE_COLOR))) {
            @Override
            public boolean shouldCollideWith(GameObject other) {
                return false;
            }
        };
        new Transition<>(leaf, leaf.renderer()::setRenderableAngle, -LEAF_ROTATION, LEAF_ROTATION,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                rand.nextFloat(MIN_REGULAR_TRANSITION_TIME, MAX_REGULAR_TRANSITION_TIME),
                TransitionType.TRANSITION_BACK_AND_FORTH, null);
        new Transition<>(leaf, leaf::setDimensions, Vector2.ONES.mult(LEAF_SIZE),
                Vector2.ONES.mult(LEAF_SIZE).multX(LEAF_WIDTH_ON_TRANSITION),
                Transition.CUBIC_INTERPOLATOR_VECTOR,
                rand.nextFloat(MIN_REGULAR_TRANSITION_TIME, MAX_REGULAR_TRANSITION_TIME),
                TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return leaf;
    }

    /**
     * Creates a stream of leaves for a given tree.
     * 
     * @param tree the tree
     * @return a stream of leaves for the given tree
     */
    public static Stream<GameObject> createLeaves(GameObject tree) {
        ArrayList<GameObject> leaves = new ArrayList<>();
        var seed = Services.getService(pepse.util.Seed.class).getSeed() + LEAVES_SEED_ADD;
        var treeTop = Vector2.of(tree.getCenter().x(), tree.getTopLeftCorner().y());
        var rand = new Random(Objects.hash(seed, treeTop.x()));
        var leafCount = rand.nextInt(MIN_LEAVES, MAX_LEAVES);
        HashSet<Vector2> leafPositions = new HashSet<>();
        for (int i = 0; i < leafCount; i++) {
            // the x position of the leaf is normally distributed around the center of the
            // top of the tree
            var leafPosition = Vector2.of((int) rand.nextGaussian(ZERO, LEAF_X_STDDEV),
                    (int) rand.nextGaussian(LEAF_Y_MEAN, LEAF_Y_STDDEV));
            if (leafPositions.contains(leafPosition)) {
                i--;
                continue;
            }
            leafPositions.add(leafPosition);
            var leaf = createLeaf(rand);
            leaf.setCenter(treeTop.add(leafPosition.mult(LEAF_SIZE)));
            leaves.add(leaf);
        }
        var gameState = Services.getService(GameState.class);
        gameState.onAvatarStateChanged().add((avatar, args) -> {
            if (args.getArgs().getNewValue() != AvatarState.JUMP) return;
            for (var leaf : leaves) {
                var curAng = leaf.renderer().getRenderableAngle();
                animateLeafRotation(rand, leaf, curAng);
            }

        });
        return leaves.stream();
    }

    /**
     * Animates the rotation of a leaf.
     * 
     * @param rand   the random number generator
     * @param leaf   the leaf
     * @param curAng the current angle of the leaf
     */
    private static void animateLeafRotation(Random rand, GameObject leaf, float curAng) {
        new Transition<>(
                leaf, leaf.renderer()::setRenderableAngle,
                curAng, curAng + FULL_ROTATION,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                rand.nextFloat(MIN_JUMPING_ANIMATION_TIME, MAX_JUMPING_ANIMATION_TIME),
                TransitionType.TRANSITION_ONCE,
                () -> leaf.renderer().setRenderableAngle(curAng));
    }

    /**
     * creates a stream of fruits for a given tree.
     * 
     * @param tree the tree
     * @return a stream of fruits for the given tree
     */
    public static Stream<GameObject> createFruits(GameObject tree) {
        ArrayList<Fruit> fruits = new ArrayList<>();
        var seed = Services.getService(pepse.util.Seed.class).getSeed() + 4;
        var treeTop = tree.getTopLeftCorner().add(tree.getDimensions().mult(0.5f).multY(0));
        var rand = new Random(Objects.hash(seed, treeTop.x()));
        var fruitCount = rand.nextInt(5, 10);
        HashSet<Vector2> fruitPositions = new HashSet<>();
        for (int i = 0; i < fruitCount; i++) {
            var fruitPosition = Vector2.of((int) rand.nextGaussian(ZERO, LEAF_X_STDDEV),
                    (int) rand.nextGaussian(LEAF_Y_MEAN, LEAF_Y_STDDEV));
            if (fruitPositions.contains(fruitPosition)) {
                i--;
                continue;
            }
            var fruit = new Fruit(treeTop.add(fruitPosition.mult(LEAF_SIZE)), tree);
            fruit.setCenter(treeTop.add(fruitPosition.mult(LEAF_SIZE)));
            fruits.add(fruit);
        }
        var gameState = Services.getService(GameState.class);
        gameState.onAvatarStateChanged().add((avatar, args) -> {
            if (args.getArgs().getNewValue() != AvatarState.JUMP)
                return;
            for (var fruit : fruits) {
                var curColor = fruit.color;
                animateFruitColorChange(rand, fruit, curColor);
            }
        });

        return fruits.stream().map(f -> (GameObject) f);
    }

    /**
     * Interpolates between two colors.
     * 
     * @param s the start color
     * @param e the end color
     * @param t the time
     * @return the interpolated color
     */
    private static void animateFruitColorChange(Random rand, Fruit fruit, Color curColor) {
        new Transition<Color>(
                fruit, c -> fruit.renderer().setRenderable(new OvalRenderable(c)),
                curColor, ColorSupplier.approximateColor(LEAF_BASE_COLOR_ON_JUMP),
                Flora::interpolateColorLineary,
                rand.nextFloat(4, 8), TransitionType.TRANSITION_ONCE,
                () -> fruit.renderer().setRenderable(new OvalRenderable(curColor)));
    }

    /**
     * a function to interpolate between two color components.
     * 
     * @param s
     * @param e
     * @param t
     * @return
     */
    private static int ci(int s, int e, float t) {
        return s + (int) ((e - s) * t);
    }

    /**
     * Interpolates between two colors.
     * 
     * @param s the start color
     * @param e the end color
     * @param t the time
     * @return the interpolated color
     */
    private static Color interpolateColorLineary(Color s, Color e, float t) {
        return new Color(
                ci(s.getRed(), e.getRed(), t), ci(s.getGreen(), e.getGreen(), t),
                ci(s.getBlue(), e.getBlue(), t), ci(s.getAlpha(), e.getAlpha(), t));
    }
}
