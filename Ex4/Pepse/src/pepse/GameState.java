package pepse;

import pepse.util.Event;
import pepse.util.EventToken;
import pepse.util.ValueChanged;
import pepse.world.Avatar.AvatarState;

/**
 * The game state.
 * 
 * @author Oryan Hassidim
 */
public class GameState {
    /**
     * The avatar state.
     */
    private AvatarState avatarState;
    /**
     * The avatar energy.
     */
    private float avatarEnergy;
    /**
     * The hour in the day.
     */
    private float hourInDay;

    /**
     * The avatar state changed event.
     */
    private Event<GameState, ValueChanged<AvatarState>> avatarStateChanged = new Event<>();
    /**
     * The avatar energy changed event.
     */
    private Event<GameState, ValueChanged<Float>> avatarEnergyChanged = new Event<>();
    /**
     * The hour in day changed event.
     */
    private Event<GameState, ValueChanged<Float>> hourInDayChanged = new Event<>();

    /**
     * Create a new game state.
     * 
     * @param avatarState  The avatar state.
     * @param avatarEnergy The avatar energy.
     * @param hourInDay    The hour in the day.
     */
    public GameState(AvatarState avatarState, float avatarEnergy, float hourInDay) {
        this.avatarState = avatarState;
        this.avatarEnergy = avatarEnergy;
        this.hourInDay = hourInDay;
    }

    /**
     * Gets the avatar state.
     * 
     * @return The avatar state.
     */
    public AvatarState getAvatarState() {
        return avatarState;
    }

    /**
     * Sets the avatar state.
     * 
     * @param avatarState The avatar state.
     */
    public void setAvatarState(AvatarState avatarState) {
        if (this.avatarState != avatarState) {
            var old = this.avatarState;
            this.avatarState = avatarState;
            avatarStateChanged.invoke(this, new ValueChanged<>(old, avatarState));
        }
    }

    /**
     * Gets the avatar energy.
     * 
     * @return The avatar energy.
     */
    public float getAvatarEnergy() {
        return avatarEnergy;
    }

    /**
     * Sets the avatar energy.
     * 
     * @param avatarEnergy The avatar energy.
     */
    public void setAvatarEnergy(float avatarEnergy) {
        if (this.avatarEnergy != avatarEnergy) {
            var old = this.avatarEnergy;
            this.avatarEnergy = avatarEnergy;
            avatarEnergyChanged.invoke(this, new ValueChanged<>(old, avatarEnergy));
        }
    }

    /**
     * Gets the hour in the day.
     * 
     * @return The hour in the day.
     */
    public float getHourInDay() {
        return hourInDay;
    }

    /**
     * Sets the hour in the day.
     * 
     * @param hourInDay The hour in the day.
     */
    public void setHourInDay(float hourInDay) {
        if (this.hourInDay != hourInDay) {
            var old = this.hourInDay;
            this.hourInDay = hourInDay;
            hourInDayChanged.invoke(this, new ValueChanged<>(old, hourInDay));
        }
    }

    /**
     * Get the event token for the avatar state changed event.
     * 
     * @return The event token.
     */
    public EventToken<GameState, ValueChanged<AvatarState>> onAvatarStateChanged() {
        return avatarStateChanged.getToken();
    }

    /**
     * Get the event token for the avatar energy changed event.
     * 
     * @return The event token.
     */
    public EventToken<GameState, ValueChanged<Float>> onAvatarEnergyChanged() {
        return avatarEnergyChanged.getToken();
    }

    /**
     * Get the event token for the hour in day changed event.
     * 
     * @return The event token.
     */
    public EventToken<GameState, ValueChanged<Float>> onHourInDayChanged() {
        return hourInDayChanged.getToken();
    }
}
