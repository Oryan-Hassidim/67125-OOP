package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.components.Transition.TransitionType;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.ui.EnergyView;
import pepse.util.Seed;
import pepse.util.Services;
import pepse.world.Avatar;
import pepse.world.GroundManager;
import pepse.world.Sky;
import pepse.world.SkyFactory;
import pepse.world.Terrain;
import pepse.world.Avatar.AvatarState;
import pepse.world.daynight.CycleLength;
import pepse.world.daynight.Night;
import pepse.world.daynight.NightFactory;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunFactory;
import pepse.world.daynight.SunHalo;
import pepse.world.daynight.SunHaloFactory;
import pepse.world.trees.Flora;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * The game manager for the Pepse game.
 * 
 * @author Oryan Hassidim
 */
public class PepseGameManager extends GameManager {
    /**
     * The layer for static objects - ground and trees.
     */
    private static final int STATIC_OBJECTS_LAYER = Layer.STATIC_OBJECTS;
    /**
     * The layer for leaves.
     */
    private static final int LEAVES_LAYER = Layer.STATIC_OBJECTS + 1;
    /**
     * The layer for fruits.
     */
    private static final int FRUITS_LAYER = Layer.DEFAULT;
    /**
     * The default window size.
     */
    private static final Vector2 DEFAULT_WINDOW_SIZE = new Vector2(800, 600);
    /**
     * The title string.
     */
    private static final String TITLE_STRING = "Pepse";
    /**
     * The zero integer.
     */
    private static final int ZERO = 0;
    /**
     * The frame rate.
     */
    private static final int FRAME_RATE = 60;

    /**
     * The input listener.
     */
    private UserInputListener inputListener;
    /**
     * The game state.
     */
    private GameState gameState;
    /**
     * The avatar.
     */
    private Avatar avatar;
    /**
     * the most left x coordinate of the game world.
     */
    private int minX;
    /**
     * the most right x coordinate of the game world.
     */
    private int maxX;

    /**
     * Create a new game manager.
     */
    public PepseGameManager() {
        super(TITLE_STRING, DEFAULT_WINDOW_SIZE);
    }

    /**
     * Create a new game manager, with a specified title.
     * 
     * @param title The title.
     */
    public PepseGameManager(String title) {
        super(title);
    }

    /**
     * Create a new game manager, with a specified title and window dimensions.
     * 
     * @param title            The title.
     * @param windowDimensions The window dimensions.
     */
    public PepseGameManager(String title, Vector2 windowDimensions) {
        super(title, windowDimensions);
    }

    /**
     * Configure the services.
     * 
     * @param imageReader      The image reader.
     * @param soundReader      The sound reader.
     * @param inputListener    The input listener.
     * @param windowController The window controller.
     */
    public void configureServices(
            ImageReader imageReader, SoundReader soundReader,
            UserInputListener inputListener, WindowController windowController) {
        Services.addSingleton(ImageReader.class, imageReader);
        Services.addSingleton(SoundReader.class, soundReader);
        Services.addSingleton(UserInputListener.class, inputListener);
        Services.addSingleton(WindowController.class, windowController);

        Services.addSingleton(SkyFactory.class, Sky::create);
        Services.addSingleton(GroundManager.class, new Terrain());
        Services.addSingleton(CycleLength.class, new CycleLength() {
        });
        Services.addSingleton(NightFactory.class, Night::create);
        Services.addSingleton(SunFactory.class, Sun::create);
        Services.addSingleton(SunHaloFactory.class, SunHalo::create);
        Services.addService(GameState.class, () -> gameState);
    }

    /**
     * Create the game objects in the given range.
     * 
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     */
    private void createInRange(int minX, int maxX) {
        var gManager = Services.getService(GroundManager.class);
        gManager.createInRange(minX, maxX)
                .forEach(b -> gameObjects().addGameObject(b, STATIC_OBJECTS_LAYER));
        var trees = Flora.createInRange(minX, maxX);
        trees.forEach(t -> gameObjects().addGameObject(t, STATIC_OBJECTS_LAYER));
        trees.stream().flatMap(Flora::createLeaves).forEach(
                l -> gameObjects().addGameObject(l, LEAVES_LAYER));
        trees.stream().flatMap(Flora::createFruits).forEach(
                f -> gameObjects().addGameObject(f, FRUITS_LAYER));
    }

    /**
     * Clear the game objects that are out of the given range.
     * 
     * @param layer the layer to clear
     * @param minX  the minimum x coordinate
     * @param maxX  the maximum x coordinate
     */
    private void clearOutOfRange(int layer, int minX, int maxX) {
        var outOfBounds = new ArrayList<GameObject>();
        for (var obj : gameObjects().objectsInLayer(layer))
            if (obj.getCenter().x() < minX || obj.getCenter().x() > maxX)
                outOfBounds.add(obj);
        outOfBounds.forEach(b -> gameObjects().removeGameObject(b, layer));
    }

    /**
     * Initialize the given range.
     * 
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     */
    private void initRange(int minX, int maxX) {
        clearOutOfRange(STATIC_OBJECTS_LAYER, minX, maxX);
        clearOutOfRange(LEAVES_LAYER, minX, maxX);
        clearOutOfRange(FRUITS_LAYER, minX, maxX);

        if (minX < this.minX) {
            createInRange(minX, this.minX);
        }
        if (maxX > this.maxX) {
            createInRange(this.maxX, maxX);
        }

        this.minX = minX;
        this.maxX = maxX;
    }

    /**
     * The method will be called once when a GameGUIComponent is created,
     * and again after every invocation of windowController.resetGame().
     * 
     * @param imageReader      Contains a single method: readImage, which reads an
     *                         image from disk. See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a
     *                         wav file from disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns
     *                         whether a given key is currently pressed by the user
     *                         or not. See its documentation.
     * @param windowController Contains an array of helpful, self explanatory
     *                         methods concerning the window.
     * @see ImageReader
     * @see SoundReader
     * @see UserInputListener
     * @see WindowController
     */
    @Override
    public void initializeGame(
            ImageReader imageReader, SoundReader soundReader,
            UserInputListener inputListener, WindowController windowController) {
        configureServices(imageReader, soundReader, inputListener, windowController);
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.inputListener = Services.getService(UserInputListener.class);
        windowController = Services.getService(WindowController.class);

        gameState = new GameState(AvatarState.IDLE, 100, 12);
        gameObjects().addGameObject(
                Services.getService(SkyFactory.class).provide(), Layer.BACKGROUND);
        gameObjects().addGameObject(
                Services.getService(NightFactory.class).provide(), Layer.FOREGROUND);
        var sun = Services.getService(SunFactory.class).provide();
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        var sunHalo = Services.getService(SunHaloFactory.class).provide(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        avatar = new Avatar(new Vector2(100, 100));
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        gameObjects().addGameObject(EnergyView.create(), Layer.UI);
        var x = (int) windowController.getWindowDimensions().x();
        createInRange(-x, 2 * x);
        this.minX = -x;
        this.maxX = 2 * x;

        new Transition<Float>(
                sun, gameState::setHourInDay, 0f, 24f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                Services.getService(CycleLength.class).provide(),
                TransitionType.TRANSITION_LOOP, null);
        this.setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        windowController.setTargetFramerate(FRAME_RATE);
    }

    /**
     * Called once per frame. Any logic is put here. Rendering, on the other hand,
     * should only be done within 'render'.
     * Note that the time that passes between subsequent calls to this method is not
     * constant.
     * 
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since
     *                  some event, or for physics integration (i.e., multiply this
     *                  by the acceleration to get an estimate of the added velocity
     *                  or by the velocity to get an estimate of the difference in
     *                  position).
     */
    @Override
    public void update(float delta) {
        if (inputListener.isKeyPressed(KeyEvent.VK_2))
            super.update(2 * delta);
        else if (inputListener.isKeyPressed(KeyEvent.VK_8))
            super.update(8 * delta);
        else
            super.update(delta);

        if (inputListener.isKeyPressed(KeyEvent.VK_P))
            gameState.setAvatarEnergy(100);

        var pos = avatar.getCenter().x();
        var x = (int) Services.getService(WindowController.class).getWindowDimensions().x();

        if (pos < minX + x)
            initRange(minX - x, maxX - x);
        else if (pos > maxX - x)
            initRange(minX + x, maxX + x);
    }

    /**
     * Main method for the game.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            final int seed = Integer.parseInt(args[0]);
            Services.addSingleton(Seed.class, () -> seed);
        } else {
            final int seed = (int) System.currentTimeMillis() % Integer.MAX_VALUE;
            Services.addSingleton(Seed.class, () -> seed);
        }

        var manager = new PepseGameManager() {
            @Override
            public void configureServices(
                    ImageReader imageReader, SoundReader soundReader,
                    UserInputListener inputListener, WindowController windowController) {
                super.configureServices(imageReader, soundReader, inputListener, windowController);

            };
        };
        manager.run();
    }
}
