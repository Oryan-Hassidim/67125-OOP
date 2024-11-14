package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.GameState;
import pepse.util.Event;
import pepse.util.EventToken;
import pepse.util.Food;
import pepse.util.None;
import pepse.util.Services;
import java.awt.event.KeyEvent;

/**
 * The avatar class for the game.
 * The avatar is the player's character in the game.
 * The avatar can move, jump, and eat food to gain energy.
 * The avatar has a state, energy, and can jump.
 * 
 * @author Oryan Hassidim
 */
public class Avatar extends GameObject {

    /**
     * enum for representing the avatar's state.
     */
    public static enum AvatarState {
        /**
         * The idle state.
         */
        IDLE,
        /**
         * The run state.
         */
        RUN,
        /**
         * The jump state.
         */
        JUMP
    }

    // constants
    /**
     * The maximum energy of the avatar.
     */
    private static final float MAX_ENERGY = 100f;
    /**
     * The avatar's size.
     */
    private static final Vector2 AVATER_SIZE = Vector2.of(40f, 50f);
    /**
     * The jump key.
     */
    private static final int JUMP_KEY = KeyEvent.VK_SPACE;
    /**
     * The right key.
     */
    private static final int RIGHT_KEY = KeyEvent.VK_RIGHT;
    /**
     * The left key.
     */
    private static final int LEFT_KEY = KeyEvent.VK_LEFT;
    /**
     * The x velocity.
     */
    private static final float VELOCITY_X = 400;
    /**
     * The y velocity.
     */
    private static final float VELOCITY_Y = -600;
    /**
     * The gravity.
     */
    private static final float GRAVITY = 600;

    /**
     * The default energy.
     */
    private static final int DEFAULT_ENERGY = 100;
    /**
     * The energy added when the avatar in idle state.
     */
    private static final float ENERGY_CONSUMPTION_IDLE = 1f;
    /**
     * The energy consumption for the run state.
     */
    private static final float ENERGY_CONSUMPTION_RUN = 0.5f;
    /**
     * The energy consumption for the jump state.
     */
    private static final float ENERGY_CONSUMPTION_JUMP = 10;

    // animation
    /**
     * The idle animation images.
     */
    private static final String[] ANIMATION_IDLE_IMAGES = new String[] {
            "assets/idle_0.png", "assets/idle_1.png",
            "assets/idle_2.png", "assets/idle_3.png" };
    /**
     * The run animation images.
     */
    private static final String[] ANIMATION_RUN_IMAGES = new String[] {
            "assets/run_0.png", "assets/run_1.png",
            "assets/run_2.png", "assets/run_3.png",
            "assets/run_4.png", "assets/run_5.png" };
    /**
     * The jump animation images.
     */
    private static final String[] ANIMATION_JUMP_IMAGES = new String[] {
            "assets/jump_0.png", "assets/jump_1.png",
            "assets/jump_2.png", "assets/jump_3.png" };
    /**
     * The animation image duration.
     */
    private static final float ANIMATION_IMAGE_DURATION = 0.5f;

    // state
    /**
     * The energy of the avatar.
     */
    private float energy = DEFAULT_ENERGY;
    /**
     * The velocity of the avatar.
     */
    private Vector2 velocity = Vector2.ZERO;
    /**
     * The state of the avatar.
     */
    private AvatarState state = AvatarState.IDLE;

    // other fields
    /**
     * The idle animation.
     */
    private AnimationRenderable idleAnimation;
    /**
     * The run animation.
     */
    private AnimationRenderable runAnimation;
    /**
     * The jump animation.
     */
    private AnimationRenderable jumpAnimation;
    /**
     * The input listener.
     */
    private UserInputListener inputListener;
    /**
     * The jump event.
     */
    private Event<Avatar, None> jumpEvent = new Event<>();
    /**
     * The game state.
     */
    private GameState gameState;

    /**
     * Get the image reader.
     * 
     * @return the image reader.
     */
    private static ImageReader imageReader() {
        return Services.getService(ImageReader.class);
    }

    /**
     * Create a new avatar.
     * 
     * @param pos the position of the avatar.
     */
    public Avatar(Vector2 pos) {
        super(pos, AVATER_SIZE, imageReader().readImage("assets/idle_0.png", true));
        this.inputListener = Services.getService(UserInputListener.class);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        idleAnimation = new AnimationRenderable(ANIMATION_IDLE_IMAGES,
                imageReader(), true, ANIMATION_IMAGE_DURATION);
        runAnimation = new AnimationRenderable(ANIMATION_RUN_IMAGES,
                imageReader(), true, ANIMATION_IMAGE_DURATION);
        jumpAnimation = new AnimationRenderable(ANIMATION_JUMP_IMAGES,
                imageReader(), true, ANIMATION_IMAGE_DURATION);
        gameState = Services.getService(GameState.class);
    }

    /**
     * Called on the first frame of a collision.
     * 
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other instanceof Food) {
            var food = (Food) other;
            energy += food.eaten();
        }
    }

    /**
     * Gets the energy of the avatar.
     * 
     * @return the energy of the avatar.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Gets the state of the avatar.
     * 
     * @return the state of the avatar.
     */
    public EventToken<Avatar, None> jumpEvent() {
        return jumpEvent.getToken();
    }

    /**
     * Should be called once per frame.
     * 
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        energy = gameState.getAvatarEnergy();
        state = AvatarState.IDLE;

        handleRunningInput();
        handleJumpInput();

        var velocity = transform().getVelocity();
        state = velocity.y() != 0 ? AvatarState.JUMP : velocity.x() != 0 ? AvatarState.RUN : AvatarState.IDLE;

        updateAnimation(velocity);

        // update state and energy
        gameState.setAvatarState(state);
        if (state == AvatarState.IDLE)
            energy += ENERGY_CONSUMPTION_IDLE;
        energy = Math.min(MAX_ENERGY, energy);
        gameState.setAvatarEnergy(energy);
    }

    /**
     * Updates the animation of the avatar.
     * 
     * @param velocity the velocity of the avatar.
     */
    private void updateAnimation(Vector2 velocity) {
        if (velocity.x() != this.velocity.x() || (velocity.y() == 0) != (this.velocity.y() == 0)) {
            if (velocity.equals(Vector2.ZERO))
                renderer().setRenderable(idleAnimation);
            else if (velocity.x() == 0)
                renderer().setRenderable(jumpAnimation);
            else {
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(velocity.x() < 0);
            }
            this.velocity = Vector2.of(velocity.x(), velocity.y());
        }
    }

    /**
     * Handles the jump input.
     */
    private void handleJumpInput() {
        if (inputListener.isKeyPressed(JUMP_KEY) && getVelocity().y() == 0
                && energy >= ENERGY_CONSUMPTION_JUMP) {
            transform().setVelocityY(VELOCITY_Y);
            energy -= ENERGY_CONSUMPTION_JUMP;
            jumpEvent.invoke(this, new None());
        }
    }

    /**
     * Handles the running input.
     */
    private void handleRunningInput() {
        float xVel = 0;
        if (inputListener.isKeyPressed(LEFT_KEY) && energy >= ENERGY_CONSUMPTION_RUN)
            xVel -= VELOCITY_X;
        if (inputListener.isKeyPressed(RIGHT_KEY) && energy >= ENERGY_CONSUMPTION_RUN)
            xVel += VELOCITY_X;
        if (xVel != 0) {
            energy -= ENERGY_CONSUMPTION_RUN;
        }
        transform().setVelocityX(xVel);
    }
}