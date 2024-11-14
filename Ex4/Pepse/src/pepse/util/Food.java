package pepse.util;

/**
 * Interface for representing food.
 * The food is eaten and returns the amount of energy it provides.
 * 
 * @author Oryan Hassidim
 */
public interface Food {
    /**
     * Eats the food.
     * 
     * @return The amount of energy the food provides.
     */
    float eaten();
}
